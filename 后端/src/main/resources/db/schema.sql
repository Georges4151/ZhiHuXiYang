-- 管理员用户表
CREATE TABLE IF NOT EXISTS `admin_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `name` varchar(50) DEFAULT NULL COMMENT '姓名',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `role` varchar(20) DEFAULT 'operator' COMMENT '角色：admin-管理人员，operator-运营成员',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_role` (`role`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员用户表';

-- 插入默认数据（如果不存在）
INSERT IGNORE INTO `admin_user` (`username`, `password`, `name`, `role`, `phone`, `email`, `status`) VALUES
('wxh', '123456', '管理员', 'admin', '13800138000', 'admin@example.com', 1),
('operator1', '123456', '运营人员1', 'operator', '13800138001', 'operator1@example.com', 1),
('operator2', '123456', '运营人员2', 'operator', '13800138002', 'operator2@example.com', 1);
