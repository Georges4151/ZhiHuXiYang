#!/bin/bash

# Rocky Linux 环境安装脚本
# 适用于智慧养老平台部署

echo "===== 开始安装 Rocky Linux 环境 ====="

# 1. 更新系统
echo "1. 更新系统..."
dnf update -y
dnf upgrade -y

# 2. 安装 EPEL 仓库（可选但推荐）
echo "2. 安装 EPEL 仓库..."
dnf install epel-release -y

# 3. 安装 JDK 17
echo "3. 安装 JDK 17..."
dnf install java-17-openjdk-devel -y

# 验证 Java 安装
java -version
ejavac -version

# 4. 安装 MySQL 8.0
echo "4. 安装 MySQL 8.0..."

# 安装 MySQL 社区版
dnf install mysql-server -y
