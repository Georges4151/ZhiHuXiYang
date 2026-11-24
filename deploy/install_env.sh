#!/bin/bash

echo "===== 开始安装环境 ====="

# 更新系统
apt update && apt upgrade -y

# 安装JDK 17
apt install openjdk-17-jdk -y
java -version

# 安装MySQL
apt install mysql-server -y

# 启动MySQL
systemctl start mysql
systemctl enable mysql

# 设置MySQL root密码
mysql -e "ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'your_password';"
mysql -e "FLUSH PRIVILEGES;"

# 创建数据库
mysql -u root -pyour_password -e "CREATE DATABASE elderly_care CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 安装Nginx（可选，用于反向代理）
apt install nginx -y
systemctl start nginx
systemctl enable nginx

echo "===== 环境安装完成 ====="
echo "MySQL密码: your_password"
echo "数据库名: elderly_care"
