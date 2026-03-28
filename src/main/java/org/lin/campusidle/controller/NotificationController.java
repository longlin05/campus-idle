package org.lin.campusidle.controller;

import org.lin.campusidle.common.result.Result;
import org.lin.campusidle.entity.Notification;
import org.lin.campusidle.service.NotificationService;
import org.lin.campusidle.vo.PageV0;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//站内信息相关方法均要添加拦截器验证
@RestController
@RequestMapping("/api/notification")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;

    //查看通知列表（用户仅能查看自己的消息）
    @GetMapping("/list")
    public Result<PageV0<Notification>> getNotificationList(@RequestParam Long receiverId, 
                                                           @RequestParam Long current, 
                                                           @RequestParam Long size) {
        return notificationService.getNotificationList(receiverId, current, size);
    }

    //查看未读通知数
    @GetMapping("/unread/count")
    public Result<Integer> getUnreadCount(@RequestParam Long receiverId) {
        return notificationService.getUnreadCount(receiverId);
    }

    //用户查看通知（查看通知具体内容，并将通知状态改为已读）
    @GetMapping("/detail/{notificationId}")
    public Result<Notification> getNotificationById(@PathVariable Long notificationId) {
        return notificationService.getNotificationById(notificationId);
    }

    //用户一键已读功能（按用户id查询所有接收的通知，并将所有通知状态更新为已读）
    @PostMapping("/mark-all-read")
    public Result<?> markAllAsRead(@RequestParam Long receiverId) {
        return notificationService.markAllAsRead(receiverId);
    }

    //删除通知（用户删除通知，逻辑删除）
    @DeleteMapping("/{notificationId}")
    public Result<?> deleteNotification(@PathVariable Long notificationId) {
        return notificationService.deleteNotification(notificationId);
    }

    //发送通知功能
    @PostMapping("/send")
    public Result<?> sendNotification(@RequestBody Notification notification) {
        return notificationService.sendNotification(notification);
    }

}
