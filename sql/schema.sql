CREATE DATABASE IF NOT EXISTS demo DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE demo;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `role` TINYINT NOT NULL DEFAULT 0 COMMENT '角色：0普通用户 1旅行社 2管理员',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0禁用 1正常',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 用户偏好设置表
CREATE TABLE IF NOT EXISTS `user_preference` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `budget_level` VARCHAR(20) DEFAULT 'economy' COMMENT '预算等级：economy/comfort/luxury',
    `interest_tags` VARCHAR(500) DEFAULT NULL COMMENT '兴趣标签，逗号分隔',
    `dietary_preference` VARCHAR(200) DEFAULT NULL COMMENT '饮食偏好',
    `transport_preference` VARCHAR(50) DEFAULT NULL COMMENT '交通偏好',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户偏好设置表';

-- 常用出行人员表
CREATE TABLE IF NOT EXISTS `user_companion` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `name` VARCHAR(50) NOT NULL COMMENT '姓名',
    `relationship` VARCHAR(20) DEFAULT NULL COMMENT '关系：family/friend/couple/colleague',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `id_card` VARCHAR(30) DEFAULT NULL COMMENT '身份证号',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='常用出行人员表';

-- 旅游产品表
CREATE TABLE IF NOT EXISTS `product` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `agency_id` BIGINT DEFAULT NULL COMMENT '旅行社用户ID',
    `name` VARCHAR(100) NOT NULL COMMENT '产品名称',
    `type` VARCHAR(20) NOT NULL COMMENT '类型：group/semi/self',
    `destination` VARCHAR(100) NOT NULL COMMENT '目的地',
    `days` INT NOT NULL COMMENT '行程天数',
    `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
    `price_type` VARCHAR(20) DEFAULT 'per_person' COMMENT '价格类型：per_person/per_group',
    `includes` VARCHAR(500) DEFAULT NULL COMMENT '包含项目：transport,hotel,ticket,meal等',
    `description` TEXT DEFAULT NULL COMMENT '产品描述',
    `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '封面图URL',
    `stock` INT NOT NULL DEFAULT 0 COMMENT '库存余量',
    `sold_count` INT NOT NULL DEFAULT 0 COMMENT '已售数量',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0下架 1上架',
    `start_date` DATE DEFAULT NULL COMMENT '开始日期',
    `end_date` DATE DEFAULT NULL COMMENT '结束日期',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_agency_id` (`agency_id`),
    KEY `idx_destination` (`destination`),
    KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='旅游产品表';

-- 产品标签表
CREATE TABLE IF NOT EXISTS `product_tag` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL COMMENT '标签名称',
    `category` VARCHAR(30) DEFAULT NULL COMMENT '标签分类：scene/style/season/feature',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品标签表';

-- 产品标签关联表
CREATE TABLE IF NOT EXISTS `product_tag_relation` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `product_id` BIGINT NOT NULL,
    `tag_id` BIGINT NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_product_tag` (`product_id`, `tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品标签关联表';

-- 行程规划表
CREATE TABLE IF NOT EXISTS `travel_plan` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `title` VARCHAR(100) DEFAULT NULL COMMENT '行程标题',
    `destination` VARCHAR(100) NOT NULL COMMENT '目的地',
    `days` INT NOT NULL COMMENT '天数',
    `budget` DECIMAL(10,2) DEFAULT NULL COMMENT '预算',
    `budget_level` VARCHAR(20) DEFAULT 'economy' COMMENT '预算等级',
    `interest_tags` VARCHAR(500) DEFAULT NULL COMMENT '兴趣标签',
    `companion_ids` VARCHAR(500) DEFAULT NULL COMMENT '同行人员ID，逗号分隔',
    `plan_detail` JSON DEFAULT NULL COMMENT '规划详情JSON',
    `ai_model` VARCHAR(50) DEFAULT NULL COMMENT '使用的AI模型',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0草稿 1已生成 2已收藏',
    `share_code` VARCHAR(32) DEFAULT NULL COMMENT '分享码',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_share_code` (`share_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='行程规划表';

-- 行程每日安排表
CREATE TABLE IF NOT EXISTS `travel_plan_day` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `plan_id` BIGINT NOT NULL COMMENT '规划ID',
    `day_index` INT NOT NULL COMMENT '第几天',
    `date` DATE DEFAULT NULL COMMENT '日期',
    `summary` VARCHAR(200) DEFAULT NULL COMMENT '当日概要',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_plan_id` (`plan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='行程每日安排表';

-- 行程项目表（景点/酒店/餐厅）
CREATE TABLE IF NOT EXISTS `travel_plan_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `plan_day_id` BIGINT NOT NULL COMMENT '每日安排ID',
    `item_type` VARCHAR(20) NOT NULL COMMENT '类型：scenic/hotel/restaurant/transport',
    `name` VARCHAR(100) NOT NULL COMMENT '名称',
    `address` VARCHAR(255) DEFAULT NULL COMMENT '地址',
    `start_time` TIME DEFAULT NULL COMMENT '开始时间',
    `end_time` TIME DEFAULT NULL COMMENT '结束时间',
    `cost` DECIMAL(10,2) DEFAULT NULL COMMENT '预估费用',
    `description` TEXT DEFAULT NULL COMMENT '描述',
    `longitude` DECIMAL(10,6) DEFAULT NULL COMMENT '经度',
    `latitude` DECIMAL(10,6) DEFAULT NULL COMMENT '纬度',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_plan_day_id` (`plan_day_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='行程项目表';

-- 行程产品推荐表
CREATE TABLE IF NOT EXISTS `plan_product_recommend` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `plan_id` BIGINT NOT NULL COMMENT '规划ID',
    `product_id` BIGINT NOT NULL COMMENT '产品ID',
    `plan_day_id` BIGINT DEFAULT NULL COMMENT '关联的某天',
    `match_reason` VARCHAR(500) DEFAULT NULL COMMENT '匹配原因',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0待查看 1已查看 2已加入购物车 3已忽略',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_plan_id` (`plan_id`),
    KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='行程产品推荐表';

-- 订单表
CREATE TABLE IF NOT EXISTS `orders` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `plan_id` BIGINT DEFAULT NULL COMMENT '关联规划ID',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '总金额',
    `pay_amount` DECIMAL(10,2) DEFAULT NULL COMMENT '实付金额',
    `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `pay_method` VARCHAR(20) DEFAULT NULL COMMENT '支付方式：wechat/alipay/card',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0待确认 1已确认 2已出团 3已完成 4退款中 5已退款 6已取消',
    `contact_name` VARCHAR(50) DEFAULT NULL COMMENT '联系人姓名',
    `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系人电话',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单明细表
CREATE TABLE IF NOT EXISTS `order_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `product_id` BIGINT NOT NULL COMMENT '产品ID',
    `product_name` VARCHAR(100) NOT NULL COMMENT '产品名称',
    `product_type` VARCHAR(20) DEFAULT NULL COMMENT '产品类型',
    `price` DECIMAL(10,2) NOT NULL COMMENT '单价',
    `quantity` INT NOT NULL DEFAULT 1 COMMENT '数量',
    `travel_date` DATE DEFAULT NULL COMMENT '出行日期',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- 评价表
CREATE TABLE IF NOT EXISTS `review` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `product_id` BIGINT NOT NULL COMMENT '产品ID',
    `rating` TINYINT NOT NULL COMMENT '评分1-5',
    `content` TEXT DEFAULT NULL COMMENT '评价内容',
    `images` VARCHAR(1000) DEFAULT NULL COMMENT '评价图片URL，逗号分隔',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0隐藏 1显示',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

-- 行程收藏表
CREATE TABLE IF NOT EXISTS `plan_favorite` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `plan_id` BIGINT NOT NULL COMMENT '规划ID',
    `plan_title` VARCHAR(100) DEFAULT NULL COMMENT '行程标题快照',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_plan` (`user_id`, `plan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='行程收藏表';

-- 行程分享表
CREATE TABLE IF NOT EXISTS `plan_share` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `plan_id` BIGINT NOT NULL COMMENT '规划ID',
    `share_user_id` BIGINT NOT NULL COMMENT '分享人ID',
    `share_code` VARCHAR(32) NOT NULL COMMENT '分享码',
    `view_count` INT NOT NULL DEFAULT 0 COMMENT '查看次数',
    `expire_time` DATETIME DEFAULT NULL COMMENT '过期时间',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0失效 1有效',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_share_code` (`share_code`),
    KEY `idx_plan_id` (`plan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='行程分享表';

-- AI调用日志表
CREATE TABLE IF NOT EXISTS `ai_call_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `plan_id` BIGINT DEFAULT NULL COMMENT '规划ID',
    `model` VARCHAR(50) DEFAULT NULL COMMENT 'AI模型',
    `prompt` TEXT DEFAULT NULL COMMENT '输入提示',
    `response` TEXT DEFAULT NULL COMMENT 'AI响应',
    `token_count` INT DEFAULT NULL COMMENT 'Token消耗',
    `duration_ms` INT DEFAULT NULL COMMENT '耗时毫秒',
    `is_success` TINYINT NOT NULL DEFAULT 1 COMMENT '是否成功',
    `error_msg` VARCHAR(500) DEFAULT NULL COMMENT '错误信息',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI调用日志表';