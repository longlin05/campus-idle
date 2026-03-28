package org.lin.campusidle.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("idle_category")
public class Category {
    @TableId(type = IdType.AUTO)
    private Long categoryId;
    
    private String categoryName;
    
    private Integer sortOrder;
}