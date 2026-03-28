package org.lin.campusidle.service.Impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lin.campusidle.common.result.Result;
import org.lin.campusidle.entity.Notification;
import org.lin.campusidle.mapper.NotificationMapper;
import org.lin.campusidle.vo.PageV0;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceImplTest {

    @Mock
    private NotificationMapper notificationMapper;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Notification notification;
    private List<Notification> notificationList;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        notification = new Notification();
        notification.setNotificationId(1L);
        notification.setReceiverId(1L);
        notification.setSenderId(2L);
        notification.setTitle("测试通知");
        notification.setContent("测试通知内容");
        notification.setType(1);
        notification.setIsRead(0);
        notification.setCreateTime(new Date());
        notification.setUpdateTime(new Date());
        notification.setIsDeleted(0);

        notificationList = new ArrayList<>();
        notificationList.add(notification);
    }

    @Test
    void testGetNotificationList() {
        // 测试查询通知列表
        when(notificationMapper.findByReceiverId(anyLong())).thenReturn(notificationList);

        Result<PageV0<Notification>> result = notificationService.getNotificationList(1L, 1L, 10L);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getTotal());
    }

    @Test
    void testGetNotificationById() {
        // 测试查询通知详情
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(null);
        when(redisTemplate.hasKey(anyString())).thenReturn(false);
        when(notificationMapper.findByNotificationId(anyLong())).thenReturn(notification);
        when(notificationMapper.updateById(any())).thenReturn(1);
        when(valueOperations.decrement(anyString())).thenReturn(0L);
        when(redisTemplate.expire(anyString(), anyLong(), any())).thenReturn(true);

        Result<Notification> result = notificationService.getNotificationById(1L);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals("测试通知", result.getData().getTitle());
    }

    @Test
    void testGetUnreadCount() {
        // 测试获取未读通知数
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(null);
        when(notificationMapper.countUnread(anyLong())).thenReturn(5);

        Result<Integer> result = notificationService.getUnreadCount(1L);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(5, result.getData());
    }

    @Test
    void testMarkAllAsRead() {
        // 测试一键已读功能
        when(notificationMapper.batchMarkAsRead(anyLong())).thenReturn(1);
        when(redisTemplate.delete(anyString())).thenReturn(true);

        Result<?> result = notificationService.markAllAsRead(1L);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
    }

    @Test
    void testDeleteNotification() {
        // 测试删除通知
        when(notificationMapper.findByNotificationId(anyLong())).thenReturn(notification);
        when(notificationMapper.updateById(any())).thenReturn(1);
        when(redisTemplate.delete(anyString())).thenReturn(true);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.decrement(anyString())).thenReturn(0L);
        when(redisTemplate.expire(anyString(), anyLong(), any())).thenReturn(true);

        Result<?> result = notificationService.deleteNotification(1L);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
    }

    @Test
    void testSendNotification() {
        // 测试发送通知
        when(notificationMapper.insert(any())).thenReturn(1);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString())).thenReturn(1L);
        when(redisTemplate.expire(anyString(), anyLong(), any())).thenReturn(true);

        Result<?> result = notificationService.sendNotification(notification);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
    }
}
