-- =============================================
-- 密码加密脚本
-- 说明：此脚本用于将数据库中的明文密码转换为 BCrypt 加密密码
-- 注意：BCrypt 加密后的密码格式为 $2a$10$xxxxxxxxxxxxxxxxxxxxxx
-- 使用方法：在 Java 应用中运行加密工具类执行加密
-- =============================================

-- 查看当前管理员密码（仅用于验证，生产环境请勿执行）
SELECT id, username, password, 
       CASE 
           WHEN password LIKE '$2a$%' THEN '已加密'
           ELSE '未加密（明文）'
       END as password_status
FROM admin_user;

-- 查看当前用户密码状态
SELECT id, phone, name,
       CASE 
           WHEN password LIKE '$2a$%' THEN '已加密'
           ELSE '未加密（明文）'
       END as password_status
FROM elderly_user;

-- =============================================
-- 重要提示：
-- 1. 不要直接在 SQL 中执行加密（BCrypt 需要 Java 代码）
-- 2. 请使用项目提供的密码加密工具类进行加密
-- 3. 加密前请务必备份数据库
-- =============================================

-- 备份现有密码（执行加密前）
CREATE TABLE IF NOT EXISTS admin_user_password_backup AS 
SELECT * FROM admin_user;

CREATE TABLE IF NOT EXISTS elderly_user_password_backup AS 
SELECT * FROM elderly_user;

-- 加密后的密码示例（$2a$10$ 开头）
-- 默认密码 123456 加密后类似：$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
-- 注意：每次加密结果都不同（因为盐值随机）
