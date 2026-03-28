package org.lin.campusidle.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("user_notification")
public class Notification {
    @TableId(type = IdType.AUTO)
    private Long notificationId;
    
    private Long receiverId;
    
    private Long senderId;
    
    private String title;
    
    private String content;
    
    private Integer type;
    
    private Integer isRead;
    
    private Date createTime;
    
    private Date updateTime;
    
    @TableLogic
    private Integer isDeleted;
}