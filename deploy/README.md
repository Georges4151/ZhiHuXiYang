# 智慧养老小程序部署指南

## 一、服务器准备

### 1. 购买云服务器
- 推荐：阿里云、腾讯云、华为云
- 配置：2核2G 或以上
- 系统：Ubuntu 22.04
- 价格：新用户约 99元/年

### 2. 安全组配置
开放以下端口：
- 22 (SSH)
- 80 (HTTP)
- 443 (HTTPS)
- 8080 (后端服务)

---

## 二、环境安装

### 1. 连接服务器
```bash
ssh root@你的服务器IP
```

### 2. 上传并执行安装脚本
```bash
# 上传 install_env.sh 到服务器
# 执行安装
chmod +x install_env.sh
./install_env.sh
```

### 3. 导入数据库
```bash
# 上传 create_tables.sql 到服务器
mysql -u root -p elderly_care < create_tables.sql
```

---

## 三、部署后端

### 1. 本地打包
```bash
cd d:\养老\后端
mvn clean package -DskipTests
```
生成文件：`target/elderly-0.0.1-SNAPSHOT.jar`

### 2. 上传到服务器
```bash
# 上传 jar 文件和启动脚本
scp target/elderly-0.0.1-SNAPSHOT.jar root@服务器IP:/opt/elderly/
scp deploy/start.sh root@服务器IP:/opt/elderly/
scp deploy/stop.sh root@服务器IP:/opt/elderly/
```

### 3. 启动服务
```bash
cd /opt/elderly
chmod +x start.sh stop.sh
./start.sh
```

### 4. 查看日志
```bash
tail -f app.log
```

---

## 四、配置域名（可选）

### 1. 购买域名
阿里云、腾讯云均可购买

### 2. 备案
国内服务器需要ICP备案（约7-20天）

### 3. 配置HTTPS
```bash
# 安装 certbot
apt install certbot python3-certbot-nginx

# 申请证书
certbot --nginx -d your-domain.com

# 自动续期
certbot renew --dry-run
```

### 4. Nginx配置
```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    location /api/ {
        proxy_pass http://127.0.0.1:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

---

## 五、小程序配置

### 1. 修改后端地址
编辑 `miniprogram/utils/request.js`：
```javascript
const BASE_URL = 'http://你的服务器IP:8080';
// 或使用域名
const BASE_URL = 'https://your-domain.com';
```

### 2. 配置服务器域名
登录小程序后台 → 开发 → 开发设置 → 服务器域名
添加：`https://your-domain.com`

### 3. 上传代码
微信开发者工具 → 上传

### 4. 设为体验版
小程序后台 → 版本管理 → 选为体验版本

---

## 六、常用命令

```bash
# 查看应用状态
ps -ef | grep elderly

# 查看日志
tail -f /opt/elderly/app.log

# 重启应用
cd /opt/elderly && ./stop.sh && ./start.sh

# 查看端口占用
netstat -tlnp | grep 8080
```

---

## 七、问题排查

### 1. 无法访问
- 检查安全组端口是否开放
- 检查防火墙：`ufw status`
- 检查应用是否启动：`ps -ef | grep elderly`

### 2. 数据库连接失败
- 检查MySQL是否启动：`systemctl status mysql`
- 检查用户名密码是否正确
- 检查数据库是否存在

### 3. 小程序请求失败
- 检查服务器域名是否配置
- 检查HTTPS证书是否有效
- 检查后端是否正常运行
