-- 创建数据库（如果需要）
-- CREATE DATABASE IF NOT EXISTS my_push_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- USE my_push_system;

-- 卡片收藏表
-- 保持不变，因为FavoriteCard本身可以被多个PushConfig关联（如果PushConfig表没有favorite_id的唯一约束）
-- 但根据您的新需求，现在一个favorite_id只能有一个有效的PushConfig
CREATE TABLE `favorite_card` (
                                 `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '自增主键',
                                 `content` TEXT NOT NULL COMMENT '卡片类型或者问题内容',
                                 `business_type` TINYINT NOT NULL COMMENT '1卡片 0问题',
                                 `enable_flag` TINYINT NOT NULL DEFAULT '1' COMMENT '1有效 0无效',
                                 `create_user` BIGINT NOT NULL DEFAULT '-1' COMMENT '创建人',
                                 `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `update_user` BIGINT NOT NULL DEFAULT '-1' COMMENT '更新人',
                                 `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 PRIMARY KEY (`id`),
    -- 考虑对 content 和 business_type 组合添加唯一索引，确保同一内容的卡片只能有一条有效记录
    -- 这是一个业务层面的约束，数据库层面可以支持多条相同内容但不同ID的收藏。
    -- 如果需要强制唯一，可以添加: UNIQUE KEY `uk_content_type_flag` (`content`(255),`business_type`,`enable_flag`)
    -- 但通常对TEXT字段加索引需要指定长度，或者在业务逻辑中处理。这里暂不添加，留给业务逻辑判断。
                                 UNIQUE KEY `uk_content_type_enabled` (`content`(255),`business_type`) -- 确保相同内容和类型的卡片收藏唯一
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='卡片收藏表';


-- 推送设置表
-- 关键调整：为 favorite_id 和 enable_flag 添加唯一复合索引
CREATE TABLE `push_config` (
                               `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                               `push_frequency` VARCHAR(10) NOT NULL COMMENT '枚举值 DAY WEEK MONTH',
                               `content` TEXT NOT NULL COMMENT '卡片类型或者问题',
                               `business_type` TINYINT NOT NULL COMMENT '1卡片 0问题',
                               `favorite_id` BIGINT NOT NULL COMMENT '收藏卡片ID',
                               `enable_flag` TINYINT NOT NULL DEFAULT '1' COMMENT '1有效 0无效',
                               `create_user` BIGINT NOT NULL DEFAULT '-1' COMMENT '创建人',
                               `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `update_user` BIGINT NOT NULL DEFAULT '-1' COMMENT '更新人',
                               `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               PRIMARY KEY (`id`),
    -- 外键约束
                               CONSTRAINT `fk_push_config_favorite` FOREIGN KEY (`favorite_id`) REFERENCES `favorite_card` (`id`),
    -- ！！！新增唯一索引：确保对于同一个 favorite_id，只能有一条 enable_flag 为 1 的记录
                               UNIQUE KEY `uk_favorite_id_enabled` (`favorite_id`, `enable_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='推送设置表';

-- 推送时间表
-- 保持不变
CREATE TABLE `push_time` (
                             `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键自增',
                             `push_config_id` BIGINT NOT NULL COMMENT '推送设置表ID',
                             `weekday` VARCHAR(20) DEFAULT NULL COMMENT '周 1-7 用逗号分隔 例如 1,3,5 代表周一周三周五',
                             `month_day` VARCHAR(60) DEFAULT NULL COMMENT '日期 1-31 用逗号分隔1,3,8 代表1号 3号 8号',
                             `hour` VARCHAR(60) NOT NULL COMMENT '时间 0点到23点 整点 用逗号分隔 最多三个时间 例如00：00,15:00,17:00',
                             `enable_flag` TINYINT NOT NULL DEFAULT '1' COMMENT '1有效 0无效',
                             `create_user` BIGINT NOT NULL DEFAULT '-1' COMMENT '创建人',
                             `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `update_user` BIGINT NOT NULL DEFAULT '-1' COMMENT '更新人',
                             `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             PRIMARY KEY (`id`),
                             KEY `idx_push_config_id` (`push_config_id`),
                             CONSTRAINT `fk_push_time_config` FOREIGN KEY (`push_config_id`) REFERENCES `push_config` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='推送时间表';

-- 消息表 (新增)
CREATE TABLE `message_log` (
                               `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '自增主键',
                               `favorite_id` BIGINT NOT NULL COMMENT '收藏卡片ID',
                               `message_content` TEXT NOT NULL COMMENT '消息内容',
                               `enable_flag` TINYINT NOT NULL DEFAULT '1' COMMENT '1有效 0无效',
                               `create_user` BIGINT NOT NULL DEFAULT '-1' COMMENT '创建人',
                               `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `update_user` BIGINT NOT NULL DEFAULT '-1' COMMENT '更新人',
                               `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               PRIMARY KEY (`id`),
                               KEY `idx_favorite_id_msg` (`favorite_id`),
                               CONSTRAINT `fk_message_log_favorite` FOREIGN KEY (`favorite_id`) REFERENCES `favorite_card` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息表';