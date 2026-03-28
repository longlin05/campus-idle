package org.lin.campusidle.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("idle_product_image")
public class ProductImage {
    @TableId(type = IdType.AUTO)
    private Long imageId;
    
    private Long productId;
    
    private String imageUrl;
    
    private Integer sortOrder;
}