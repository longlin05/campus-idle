package org.lin.campusidle.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("sys_user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long userId;
    
    private String username;
    
    @TableField(select = false) // 密码不参与查询
    private String password;
    
    private String nickname;
    
    private String avatar;
    
    private String phone;
    
    private String email;
    
    private Integer role;
    
    private Integer status;
    
    private Date lastLoginTime;
    
    private String lastLoginIp;
    
    private Date createTime;
    
    private Date updateTime;
    
    @TableLogic
    private Integer isDeleted;
}