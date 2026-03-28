package org.lin.campusidle.service;

import org.lin.campusidle.common.result.Result;
import org.lin.campusidle.entity.Notification;
import org.lin.campusidle.vo.PageV0;


public interface NotificationService {

    //查询通知功能（按接收用户id查询）
    Result<PageV0<Notification>> getNotificationList(Long receiverId, Long current, Long size);

    //查询通知信息功能（根据通知id查询通知）
    Result<Notification> getNotificationById(Long notificationId);

    //统计未读通知数，以redis做快速存储收到一条消息就+1，一键已读后标记为0，MySQL做持久化，更新策略每半小时更新一次数据库，先修改数据库再删除redis缓存
    Result<Integer> getUnreadCount(Long receiverId);

    //一键已读功能，消息状态在redis存储，将按用户id查询到的所有消息全部标记为已读，后续异步修改数据库数据
    Result<?> markAllAsRead(Long receiverId);

    //删除消息功能（逻辑删除）
    Result<?> deleteNotification(Long notificationId);

    //发送通知功能
    Result<?> sendNotification(Notification notification);

}
