-- 用户表（对应JWT+RBAC权限控制）
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
        `user_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户主键ID',
        `username` VARCHAR(32) NOT NULL COMMENT '登录用户名（唯一）',
        `password` VARCHAR(128) NOT NULL COMMENT '加密密码（BCrypt加密，禁止明文存储）',
        `nickname` VARCHAR(32) NOT NULL COMMENT '用户昵称',
        `avatar` VARCHAR(512) DEFAULT '' COMMENT '头像地址（阿里OSS）',
        `phone` VARCHAR(11) DEFAULT '' COMMENT '手机号',
        `email` VARCHAR(64) DEFAULT '' COMMENT '邮箱',
        `role` TINYINT NOT NULL DEFAULT 1 COMMENT '用户角色：0-管理员1-普通用户',
        `status` TINYINT NOT NULL DEFAULT 1 COMMENT '账号状态：0-禁用1-正常',
        `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
        `last_login_ip` VARCHAR(32) DEFAULT '' COMMENT '最后登录IP',
        `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
        `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
        `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除1-已删除',
        PRIMARY KEY (`user_id`),
        UNIQUE KEY `uk_username` (`username`),
        UNIQUE KEY `uk_phone` (`phone`),
        UNIQUE KEY `uk_email` (`email`),
        KEY `idx_role_status` (`role`, `status`),
        KEY `idx_status` (`status`),
        KEY `idx_create_time` (`create_time`),
        KEY `idx_last_login_time` (`last_login_time`),
        KEY `idx_email` (`email`),
        KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';


DROP TABLE IF EXISTS `idle_category`;
CREATE TABLE `idle_category` (
         `category_id` TINYINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
         `category_name` VARCHAR(32) NOT NULL COMMENT '分类名称',
         `sort_order` TINYINT NOT NULL DEFAULT 0 COMMENT '排序顺序',
         PRIMARY KEY (`category_id`),
         UNIQUE KEY `uk_category_name` (`category_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';


DROP TABLE IF EXISTS `idle_product`;
CREATE TABLE `idle_product` (
        `product_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品主键ID',
        `publish_user_id` BIGINT NOT NULL COMMENT '发布者用户ID',
        `title` VARCHAR(64) NOT NULL COMMENT '商品标题',
        `description` TEXT NOT NULL COMMENT '商品详情描述',
        `price` DECIMAL(10,2) NOT NULL COMMENT '出售价格',
        `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
        `category_id` TINYINT NOT NULL COMMENT '商品分类ID',
        `status` TINYINT NOT NULL DEFAULT 1 COMMENT '商品状态：0-下架 1-上架 2-已售出 3-违规封禁',
        `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览量',
        `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
        `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
        `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
        PRIMARY KEY (`product_id`),
        KEY `idx_publish_user` (`publish_user_id`),
        KEY `idx_category_status` (`category_id`, `status`),
        KEY `idx_create_time` (`create_time`),
        KEY `idx_status` (`status`),
        KEY `idx_price` (`price`),
        KEY `idx_view_count` (`view_count`),
        FULLTEXT KEY `idx_title_fulltext` (`title`),
        FOREIGN KEY (`publish_user_id`) REFERENCES `sys_user` (`user_id`),
        FOREIGN KEY (`category_id`) REFERENCES `idle_category` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='闲置商品表';


DROP TABLE IF EXISTS `idle_product_image`;
CREATE TABLE `idle_product_image` (
          `image_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '图片ID',
          `product_id` BIGINT NOT NULL COMMENT '商品ID',
          `image_url` VARCHAR(512) NOT NULL COMMENT '图片地址（阿里OSS）',
          `sort_order` TINYINT NOT NULL DEFAULT 0 COMMENT '排序顺序',
          PRIMARY KEY (`image_id`),
          KEY `idx_product_id` (`product_id`),
          FOREIGN KEY (`product_id`) REFERENCES `idle_product` (`product_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品图片表';



DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info` (
          `order_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单主键ID',
          `order_no` VARCHAR(32) NOT NULL COMMENT '订单编号（唯一）',
          `product_id` BIGINT NOT NULL COMMENT '商品ID（关联idle_product.product_id）',
          `buyer_id` BIGINT NOT NULL COMMENT '买家用户ID（下单人）',
          `seller_id` BIGINT NOT NULL COMMENT '卖家用户ID（商品发布者）',
          `order_amount` DECIMAL(10,2) NOT NULL COMMENT '订单金额',
          `status` TINYINT NOT NULL DEFAULT 0 COMMENT '订单状态：0-待支付 1-已支付 2-已发货 3-已完成 4-已取消',
          `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
          `cancel_time` DATETIME DEFAULT NULL COMMENT '取消时间',
          `remark` VARCHAR(255) DEFAULT '' COMMENT '订单备注',
          `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
          `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
          `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
          PRIMARY KEY (`order_id`),
          UNIQUE KEY `uk_order_no` (`order_no`),
          KEY `idx_buyer_id` (`buyer_id`),
          KEY `idx_seller_id` (`seller_id`),
          KEY `idx_product_id` (`product_id`),
          KEY `idx_status_create_time` (`status`, `create_time`),
          KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='交易订单表';


DROP TABLE IF EXISTS `user_notification`;
CREATE TABLE `user_notification` (
         `notification_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '通知主键ID',
         `receiver_id` BIGINT NOT NULL COMMENT '接收人用户ID',
         `sender_id` BIGINT DEFAULT 0 COMMENT '发送人用户ID，0为系统发送',
         `title` VARCHAR(32) NOT NULL COMMENT '通知标题',
         `content` VARCHAR(255) NOT NULL COMMENT '通知内容',
         `type` TINYINT NOT NULL DEFAULT 1 COMMENT '通知类型：1-订单通知 2-系统通知 3-交易提醒',
         `is_read` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读：0-未读 1-已读',
         `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
         `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
         `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
         PRIMARY KEY (`notification_id`),
         KEY `idx_receiver_is_read` (`receiver_id`, `is_read`),
         KEY `idx_receiver_create_time` (`receiver_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户站内信表';
