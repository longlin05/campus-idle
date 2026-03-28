package org.lin.campusidle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.lin.campusidle.entity.Notification;

import java.util.List;

@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
    //根据接收用户ID查询通知列表
    @Select("select * from user_notification where receiver_id=#{receiverId} and is_deleted=0 order by create_time desc")
    List<Notification> findByReceiverId(Long receiverId);
    
    //根据通知ID查询通知
    @Select("select * from user_notification where notification_id=#{notificationId}")
    Notification findByNotificationId(Long notificationId);
    
    //统计未读通知数
    @Select("select count(*) from user_notification where receiver_id=#{receiverId} and is_read=0 and is_deleted=0")
    Integer countUnread(Long receiverId);
    
    //批量更新通知为已读
    @Update("update user_notification set is_read=1, update_time=now() where receiver_id=#{receiverId} and is_read=0 and is_deleted=0")
    int batchMarkAsRead(Long receiverId);
}