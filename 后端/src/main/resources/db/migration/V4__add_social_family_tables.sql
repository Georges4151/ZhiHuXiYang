-- 社交动态表
CREATE TABLE IF NOT EXISTS `social_post` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `user_name` VARCHAR(50) DEFAULT NULL COMMENT '用户名称',
    `user_avatar` VARCHAR(255) DEFAULT NULL COMMENT '用户头像',
    `content` TEXT NOT NULL COMMENT '动态内容',
    `images` VARCHAR(1000) DEFAULT NULL COMMENT '图片URL，多个用逗号分隔',
    `likes` INT DEFAULT 0 COMMENT '点赞数',
    `comments` INT DEFAULT 0 COMMENT '评论数',
    `category` INT DEFAULT 0 COMMENT '分类：0-全部，1-生活分享，2-健康养生，3-兴趣爱好',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_category` (`category`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社交动态表';

-- 动态点赞表
CREATE TABLE IF NOT EXISTS `social_post_like` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `post_id` BIGINT NOT NULL COMMENT '动态ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_post_user` (`post_id`, `user_id`),
    KEY `idx_post_id` (`post_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动态点赞表';

-- 动态评论表
CREATE TABLE IF NOT EXISTS `social_post_comment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `post_id` BIGINT NOT NULL COMMENT '动态ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `user_name` VARCHAR(50) DEFAULT NULL COMMENT '用户名称',
    `user_avatar` VARCHAR(255) DEFAULT NULL COMMENT '用户头像',
    `content` TEXT NOT NULL COMMENT '评论内容',
    `parent_id` BIGINT DEFAULT NULL COMMENT '父评论ID，用于回复',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`),
    KEY `idx_post_id` (`post_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动态评论表';

-- 兴趣小组表
CREATE TABLE IF NOT EXISTS `interest_group` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(100) NOT NULL COMMENT '小组名称',
    `description` TEXT COMMENT '小组描述',
    `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '封面图片',
    `category` INT DEFAULT 0 COMMENT '分类：0-其他，1-书法绘画，2-音乐舞蹈，3-运动健身，4-棋牌游戏，5-手工制作',
    `creator_id` BIGINT NOT NULL COMMENT '创建者ID',
    `creator_name` VARCHAR(50) DEFAULT NULL COMMENT '创建者名称',
    `member_count` INT DEFAULT 0 COMMENT '成员数',
    `post_count` INT DEFAULT 0 COMMENT '帖子数',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category`),
    KEY `idx_creator_id` (`creator_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='兴趣小组表';

-- 小组成员表
CREATE TABLE IF NOT EXISTS `group_member` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `group_id` BIGINT NOT NULL COMMENT '小组ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `user_name` VARCHAR(50) DEFAULT NULL COMMENT '用户名称',
    `user_avatar` VARCHAR(255) DEFAULT NULL COMMENT '用户头像',
    `role` TINYINT DEFAULT 0 COMMENT '角色：0-成员，1-群主，2-管理员',
    `joined_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_group_user` (`group_id`, `user_id`),
    KEY `idx_group_id` (`group_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='小组成员表';

-- 活动报名表
CREATE TABLE IF NOT EXISTS `activity_registration` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `activity_id` BIGINT NOT NULL COMMENT '活动ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `user_name` VARCHAR(50) DEFAULT NULL COMMENT '用户名称',
    `user_phone` VARCHAR(20) DEFAULT NULL COMMENT '用户电话',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0-待确认，1-已确认，2-已取消',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `registered_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_activity_user` (`activity_id`, `user_id`),
    KEY `idx_activity_id` (`activity_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动报名表';

-- 家属通知表
CREATE TABLE IF NOT EXISTS `family_notification` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '老人用户ID',
    `family_user_id` BIGINT NOT NULL COMMENT '家属用户ID',
    `type` VARCHAR(50) NOT NULL COMMENT '通知类型：emergency-紧急呼叫，health-健康异常，service-服务状态',
    `title` VARCHAR(200) NOT NULL COMMENT '通知标题',
    `content` TEXT COMMENT '通知内容',
    `data` TEXT COMMENT '附加数据，JSON格式',
    `is_read` TINYINT DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`),
    KEY `idx_family_user_id` (`family_user_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_type` (`type`),
    KEY `idx_is_read` (`is_read`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='家属通知表';

-- 插入默认兴趣小组数据
INSERT INTO `interest_group` (`name`, `description`, `category`, `creator_id`, `member_count`, `status`) VALUES
('书法绘画交流群', '喜欢书法绘画的朋友们一起交流学习', 1, 1, 15, 1),
('音乐舞蹈天地', '唱歌跳舞，快乐生活', 2, 1, 23, 1),
('太极拳健身队', '每天晨练，强身健体', 3, 1, 30, 1),
('棋牌娱乐室', '象棋、围棋、扑克，休闲娱乐', 4, 1, 18, 1),
('手工制作坊', '编织、剪纸、手工DIY', 5, 1, 12, 1),
('健康养生讨论', '分享养生知识，交流健康心得', 0, 1, 45, 1);
