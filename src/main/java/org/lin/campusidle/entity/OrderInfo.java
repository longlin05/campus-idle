package org.lin.campusidle.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("order_info")
public class OrderInfo {
    @TableId(type = IdType.AUTO)
    private Long orderId;
    
    private String orderNo;
    
    private Long productId;
    
    private Long buyerId;
    
    private Long sellerId;
    
    private BigDecimal orderAmount;
    
    private Integer status;
    
    private Date payTime;
    
    private Date cancelTime;
    
    private String remark;
    
    private Date createTime;
    
    private Date updateTime;
    
    @TableLogic
    private Integer isDeleted;
}