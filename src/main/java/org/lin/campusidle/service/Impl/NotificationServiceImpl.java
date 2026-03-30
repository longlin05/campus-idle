package org.lin.campusidle.service.Impl;

import org.lin.campusidle.common.result.Result;
import org.lin.campusidle.entity.Notification;
import org.lin.campusidle.mapper.NotificationMapper;
import org.lin.campusidle.service.NotificationService;
import org.lin.campusidle.vo.PageV0;
import org.lin.campusidle.common.util.RedisUtils;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private RedisUtils redisUtils;

    // Redis键前缀
    private static final String UNREAD_COUNT_KEY = "notification:unread:count:";
    private static final String NOTIFICATION_KEY = "notification:";
    private static final long REDIS_EXPIRE_TIME = 7 * 24 * 60 * 60; // 7天过期

    //查询通知功能（按接收用户id查询）
    @Override
    public Result<PageV0<Notification>> getNotificationList(Long receiverId, Long current, Long size) {
        List<Notification> notificationList = notificationMapper.findByReceiverId(receiverId);
        
        // 实现分页
        int start = (int) ((current - 1) * size);
        int end = (int) (start + size);
        if (start >= notificationList.size()) {
            return Result.success(new PageV0<>());
        }
        if (end > notificationList.size()) {
            end = notificationList.size();
        }
        List<Notification> pageList = notificationList.subList(start, end);
        
        PageV0<Notification> page = new PageV0<>();
        page.setCurrent(current);
        page.setSize(size);
        page.setTotal((long) notificationList.size());
        page.setPages((notificationList.size() + size - 1) / size);
        page.setRecords(pageList);
        
        return Result.success(page);
    }

    //查询通知信息功能（根据通知id查询通知）
    @Override
    public Result<Notification> getNotificationById(Long notificationId) {
        // 先从Redis缓存中获取
        String key = NOTIFICATION_KEY + notificationId;
        Object cachedObject = redisUtils.get(key);
        Notification notification = null;
        if (cachedObject != null && cachedObject instanceof Notification) {
            notification = (Notification) cachedObject;
        }
        
        if (notification == null) {
            // 缓存穿透保护：使用布隆过滤器或空值缓存
            if (redisUtils.get(key + ":null") != null) {
                return Result.error(404, "通知不存在");
            }
            
            // 从数据库查询
            notification = notificationMapper.findByNotificationId(notificationId);
            if (notification == null) {
                // 缓存空值，防止缓存穿透
                redisUtils.set(key + ":null", "1", 1, TimeUnit.HOURS);
                return Result.error(404, "通知不存在");
            }
            
            // 存入Redis，设置过期时间，防止缓存雪崩
            redisUtils.set(key, notification, REDIS_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        
        // 标记为已读
        if (notification.getIsRead() == 0) {
            notification.setIsRead(1);
            notification.setUpdateTime(new Date());
            notificationMapper.updateById(notification);
            // 更新Redis缓存
            redisUtils.set(key, notification, REDIS_EXPIRE_TIME, TimeUnit.SECONDS);
            // 减少未读计数
            decrementUnreadCount(notification.getReceiverId());
        }
        
        return Result.success(notification);
    }

    //统计未读通知数，以redis做快速存储收到一条消息就+1，一键已读后标记为0，MySQL做持久化，更新策略每半小时更新一次数据库，先修改数据库再删除redis缓存
    @Override
    public Result<Integer> getUnreadCount(Long receiverId) {
        String key = UNREAD_COUNT_KEY + receiverId;
        
        // 先从Redis获取
        Object cachedObject = redisUtils.get(key);
        Integer count = null;
        if (cachedObject != null && cachedObject instanceof Integer) {
            count = (Integer) cachedObject;
        }
        if (count != null) {
            return Result.success(count);
        }
        
        // 从数据库查询
        count = notificationMapper.countUnread(receiverId);
        // 存入Redis，设置过期时间
        redisUtils.set(key, count, REDIS_EXPIRE_TIME, TimeUnit.SECONDS);
        
        return Result.success(count);
    }

    //一键已读功能，消息状态在redis存储，将按用户id查询到的所有消息全部标记为已读，后续异步修改数据库数据
    @Override
    public Result<?> markAllAsRead(Long receiverId) {
        // 异步更新数据库
        new Thread(() -> {
            notificationMapper.batchMarkAsRead(receiverId);
        }).start();
        
        // 清除Redis缓存
        String key = UNREAD_COUNT_KEY + receiverId;
        redisUtils.delete(key);
        
        return Result.success("一键已读成功");
    }

    //删除消息功能（逻辑删除）
    @Override
    public Result<?> deleteNotification(Long notificationId) {
        Notification notification = notificationMapper.findByNotificationId(notificationId);
        if (notification == null) {
            return Result.error(404, "通知不存在");
        }
        
        // 逻辑删除
        notification.setIsDeleted(1);
        notification.setUpdateTime(new Date());
        notificationMapper.updateById(notification);
        
        // 删除Redis缓存
        String key = NOTIFICATION_KEY + notificationId;
        redisUtils.delete(key);
        
        // 如果是未读消息，减少未读计数
        if (notification.getIsRead() == 0) {
            decrementUnreadCount(notification.getReceiverId());
        }
        
        return Result.success("删除通知成功");
    }

    //发送通知功能
    @Override
    public Result<?> sendNotification(Notification notification) {
        notification.setIsRead(0);
        notification.setIsDeleted(0);
        notification.setCreateTime(new Date());
        notification.setUpdateTime(new Date());
        
        // 保存到数据库
        notificationMapper.insert(notification);
        
        // 增加未读计数
        incrementUnreadCount(notification.getReceiverId());
        
        return Result.success("发送通知成功");
    }

    // 增加未读计数
    private void incrementUnreadCount(Long receiverId) {
        String key = UNREAD_COUNT_KEY + receiverId;
        redisUtils.incrementCount(key);
        // 设置过期时间
        redisUtils.expire(key, REDIS_EXPIRE_TIME, TimeUnit.SECONDS);
    }

    // 减少未读计数
    private void decrementUnreadCount(Long receiverId) {
        String key = UNREAD_COUNT_KEY + receiverId;
        redisUtils.decrementCount(key);
        // 设置过期时间
        redisUtils.expire(key, REDIS_EXPIRE_TIME, TimeUnit.SECONDS);
    }

}
