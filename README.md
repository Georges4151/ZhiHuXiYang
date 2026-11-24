# 智慧养老服务系统

面向社区居家养老场景的一体化服务平台，由 **微信小程序（老人 / 家属端）**、**Web 管理后台（运营端）** 和 **Spring Boot 后端服务** 三部分组成。项目解决的核心问题是：社区养老服务信息分散、老人健康数据无法连续跟踪、家属缺少远程照护手段。系统把服务下单、健康记录、紧急呼叫、家属绑定、活动组织和资讯发布收敛到同一套数据模型里，社区运营人员在后台上统一管理，老人和家属在小程序上直接使用。

## 一、功能清单

### 小程序端（老人 / 家属）

- **账号体系**：手机号注册登录、JWT 鉴权、资料与密码修改
- **AI 健康咨询**：接入 DeepSeek 对话模型，结合用户健康数据给出饮食、用药、就医建议，历史会话落库
- **健康管理**：录入血压、心率、血糖、体重等指标，查看最近记录与趋势统计，生成健康分析建议
- **居家服务**：服务列表 / 详情 / 热门推荐、下单、订单列表与状态跟踪、模拟微信支付
- **紧急呼叫**：一键呼叫并附带定位与联系人信息，后端记录呼叫状态流转
- **家属协同**：通过手机号绑定老人，查看绑定老人的健康数据、健康统计、服务订单；家庭通知与未读数
- **社区活动**：活动列表、报名 / 取消报名、点赞、评论与评论点赞
- **社交与群组**：发布动态（含图片）、点赞、评论、兴趣群组创建 / 加入 / 退出 / 成员列表
- **生活服务**：资讯浏览与详情、用药提醒、收货地址管理、天气预报、意见反馈、消息通知中心

### 管理后台（运营端）

- **数据看板**：用户数、订单数、服务数、紧急呼叫等总览指标，订单趋势折线图、服务分布饼图（ECharts）
- **业务管理**：用户、服务项目、服务订单、资讯、社区活动、健康数据、紧急呼叫、意见反馈的增删改查与状态处理
- **账号与权限**：管理员登录、双角色（`admin` 管理岗 / `operator` 运营岗）、修改密码、个人信息
- **资讯采集**：一键抓取外部养老资讯入库（Jsoup 解析 + OkHttp 请求）

## 二、技术栈

| 层次 | 技术选型 |
| --- | --- |
| 后端框架 | Java 17、Spring Boot 3.2、Spring MVC、Bean Validation |
| 数据访问 | MyBatis-Plus 3.5.5（统一 CRUD、逻辑删除、字段自动填充）、MySQL 8 |
| 鉴权与安全 | JWT（jjwt 0.12.3）、自定义拦截器、XSS 过滤器、接口限流过滤器、CORS 配置 |
| 第三方能力 | DeepSeek Chat API（健康咨询）、和风天气 API、微信支付（当前为模拟流程）、微信小程序登录 |
| 工具库 | Hutool、FastJSON2、OkHttp3、Jsoup、Lombok |
| 管理前端 | Vue 3.4、Vite 5、Vue Router 4、Pinia、Element Plus、Axios、ECharts 5、Day.js、Sass |
| 小程序端 | 原生微信小程序（自定义 tabBar、组件化页面，30+ 页面） |
| 构建与部署 | Maven、npm、Nginx、Shell / PowerShell 部署脚本、内网穿透（cpolar） |

## 三、项目亮点

以下为本人在项目中实际承担并完成的工作：

1. **数据模型设计**：独立设计 24 张业务表（用户、家属关系、健康、订单、活动、社交、通知等），统一 `deleted` 逻辑删除与时间字段规范，配套 20 余个 SQL 脚本按模块组织。
2. **接口开发**：独立完成约 90 个 REST 接口，覆盖小程序端 13 个控制器与管理端 10 个控制器，统一 `Result<T>` 返回结构与全局异常处理。
3. **AI 健康咨询链路**：设计「用户问题 + 健康数据上下文 → DeepSeek → 结构化回答入库」的调用流程，处理超时、异常兜底与历史会话分页查询。
4. **家属协同模型**：设计老人—家属双向绑定关系表与绑定校验逻辑，实现跨账号的健康数据、订单、通知查询，以及未读数统计与批量已读。
5. **订单与支付状态机**：实现服务下单、订单号生成、状态流转（待接单 / 服务中 / 已完成 / 已取消）与支付回调处理，预留真实微信支付接入点并提供本地模拟支付接口。
6. **资讯抓取**：使用 OkHttp 拉取页面、Jsoup 解析列表与正文，做去重后入库，支持手动触发与后台管理。
7. **鉴权体系**：管理端与小程序端共用 JWT 签发工具但使用独立拦截器与角色校验，实现 `admin` / `operator` 权限区分。
8. **安全加固**：编写 XSS 过滤器与基于内存计数的限流过滤器，配置 CORS 白名单，接口入参统一 `@Valid` 校验。
9. **管理后台**：基于 Vue3 + Element Plus 完成 12 个业务页面的路由权限、请求封装（统一 token 注入与错误提示）和 ECharts 数据看板。

## 四、目录结构

```text
养老/
├── 后端/                              # Spring Boot 服务
│   ├── pom.xml
│   ├── sql/                           # 建表与初始化脚本（按模块拆分）
│   │   ├── init.sql                   # 基础表 + 演示数据
│   │   ├── create_tables.sql          # 资讯/服务/订单/家属/通知
│   │   ├── admin_tables.sql           # 活动/健康/紧急呼叫/反馈
│   │   ├── create_db.sql              # 用户/咨询/用药/地址等
│   │   └── ...
│   └── src/main/
│       ├── java/com/elderly/
│       │   ├── controller/            # 小程序端接口（api/*）
│       │   │   └── admin/             # 管理端接口（admin/*）
│       │   ├── service/               # 业务层（接口 + impl）
│       │   ├── mapper/                # MyBatis-Plus Mapper
│       │   ├── entity/                # 24 个实体类
│       │   ├── dto/                   # 请求/响应对象
│       │   ├── config/                # 拦截器、CORS、数据初始化等配置
│       │   ├── filter/                # XSS、限流过滤器
│       │   ├── exception/             # 全局异常处理
│       │   ├── task/                  # 定时任务
│       │   └── util/                  # JWT、日期等工具
│       └── resources/
│           ├── application.yml        # 端口、数据源、第三方配置
│           └── db/                    # schema / 活动 / 社交迁移脚本
├── 管理平台前端/                       # Vue3 管理后台
│   ├── src/
│   │   ├── api/                       # 接口封装
│   │   ├── layout/                    # 后台布局（侧边栏、顶栏）
│   │   ├── router/                    # 路由与登录守卫
│   │   ├── stores/                    # Pinia 状态
│   │   ├── utils/                     # request 封装、工具函数
│   │   └── views/                     # 12 个业务页面
│   └── vite.config.js                 # 开发代理 /api、/admin → 8080
├── 测试/                              # 微信小程序
│   ├── project.config.json
│   └── miniprogram/
│       ├── pages/                     # 30+ 页面（健康、服务、家属、社交…）
│       ├── components/                # 通用组件
│       ├── custom-tab-bar/            # 自定义底部导航
│       └── utils/                     # request / api / pay 封装
├── deploy/                            # 部署脚本与部署指南
├── upload/                            # 文件上传落盘目录
├── 测试报告.md                        # 接口与功能测试记录
└── 项目详细介绍.md
```

## 五、运行步骤

### 1. 环境准备

- JDK 17+、Maven 3.8+
- Node.js 18+、npm
- MySQL 8.0（本机 3306）
- 微信开发者工具（运行小程序端）

### 2. 初始化数据库

```sql
-- 1) 创建数据库
CREATE DATABASE IF NOT EXISTS elderly_care
  DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

```powershell
# 2) 导入表结构与演示数据（按顺序执行，示例路径按实际调整）
cd 后端\sql
mysql -uroot -p elderly_care < init.sql
mysql -uroot -p elderly_care < create_tables.sql
mysql -uroot -p elderly_care < admin_tables.sql
mysql -uroot -p elderly_care < create_db.sql
mysql -uroot -p elderly_care < create_address.sql
mysql -uroot -p elderly_care < family_activity.sql
mysql -uroot -p elderly_care < notification.sql
```

> 提示：所有建表语句均为 `CREATE TABLE IF NOT EXISTS`，重复执行不会清空数据；`source` 命令遇到中文路径可能报错，可先把脚本复制到纯英文目录（如 `C:\temp\sql`）再导入。

### 3. 启动后端

修改 [application.yml](file:///e:/agent/养老/后端/src/main/resources/application.yml) 中的数据库账号密码，然后：

```powershell
cd 后端
mvn spring-boot:run
```

启动成功后：`http://localhost:8080`，控制台会输出 Tomcat 端口与 MyBatis 初始化信息。
首次启动时 `DataInitializer` 会自动写入资讯与服务演示数据。

### 4. 启动管理后台

```powershell
cd 管理平台前端
npm install
npm run dev
```

访问 `http://localhost:3000`，默认管理员账号：`wxh / 123456`。
Vite 已配置 `/api`、`/admin` 代理到 `http://localhost:8080`，无需额外处理跨域。

### 5. 启动小程序

1. 打开微信开发者工具，导入目录 `测试/`（`project.config.json` 已配置 `miniprogramRoot: miniprogram/`）。
2. 修改 `miniprogram/app.js` 中的 `baseUrl` 为本地后端地址（如 `http://localhost:8080`）。
3. 详情 → 本地设置，勾选「不校验合法域名、web-view（业务域名）、TLS 版本以及 HTTPS 证书」。
4. 编译运行，使用注册/登录页创建账号后即可体验。

## 六、接口文档

### 统一返回结构

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

`code` 为业务状态码：`200` 成功，`401` 未登录/登录失效，`500` 业务或系统异常。

### 鉴权方式

管理端接口需在请求头携带登录返回的 token：

```text
Authorization: Bearer <token>
```

### 核心接口一览

| 模块 | 方法 | 路径 | 说明 |
| --- | --- | --- | --- |
| 管理端登录 | POST | `/admin/auth/login` | 用户名密码登录，返回 token 与用户信息 |
| 管理端信息 | GET | `/admin/auth/info` | 根据 token 获取当前管理员信息 |
| 仪表盘 | GET | `/admin/stats/overview` | 用户/订单/服务等总览指标 |
| 订单趋势 | GET | `/admin/stats/order-trend` | 按日期统计订单量，供折线图使用 |
| 服务分布 | GET | `/admin/stats/service-distribution` | 各服务类型占比，供饼图使用 |
| 用户管理 | GET | `/admin/user/list` | 分页查询用户（keyword/page/pageSize） |
| 服务管理 | GET/POST/PUT/DELETE | `/admin/service`、`/admin/service/{id}` | 服务项目增删改查 |
| 订单管理 | GET | `/admin/order/list` | 订单分页与状态筛选 |
| 订单状态 | PUT | `/admin/order/{id}/status` | 接单 / 完成 / 取消 |
| 资讯管理 | GET/POST/PUT/DELETE | `/admin/news`、`/admin/news/{id}` | 资讯增删改查与置顶 |
| 活动管理 | GET/POST/PUT/DELETE | `/admin/activity`、`/admin/activity/{id}` | 活动管理与报名统计 |
| 紧急呼叫 | GET | `/admin/emergency/list` | 呼叫记录与处理状态 |
| 反馈管理 | PUT | `/admin/feedback/{id}/reply` | 回复用户反馈 |
| 小程序登录 | POST | `/api/auth/login` | 手机号/密码登录 |
| 小程序注册 | POST | `/api/auth/register` | 新用户注册 |
| 新闻分页 | GET | `/api/news/page?pageNum=1&pageSize=10` | 已发布资讯分页 |
| 热门资讯 | GET | `/api/news/hot?limit=5` | 置顶/热度排序 |
| 资讯抓取 | GET | `/api/news/crawl` | 手动触发抓取 |
| 服务列表 | GET | `/api/service/list` | 服务项目列表 |
| 服务下单 | POST | `/api/service/order/create` | 创建服务订单 |
| 我的订单 | GET | `/api/service/order/user/{userId}` | 用户订单列表 |
| 健康录入 | POST | `/api/health/add` | 提交血压/心率/血糖等指标 |
| 健康分析 | GET | `/api/health/analysis/analyze?userId=1` | AI 健康分析建议 |
| AI 咨询 | POST | `/api/consultation/chat` | 健康问答并记录会话 |
| 咨询历史 | GET | `/api/consultation/history/{userId}` | 历史会话 |
| 紧急呼叫 | POST | `/api/emergency/call` | 发起紧急呼叫 |
| 家属绑定 | POST | `/api/family/bind` | 手机号绑定老人 |
| 家属健康 | GET | `/api/family/health-data/{userId}` | 查看绑定老人健康数据 |
| 家庭通知 | GET | `/api/family/notifications/{familyUserId}` | 通知列表与未读数 |
| 活动列表 | GET | `/api/activity/list` | 社区活动列表 |
| 活动报名 | POST | `/api/activity/join/{id}` | 报名 / 取消报名 |
| 社交动态 | GET/POST | `/api/social/posts` | 动态列表与发布 |
| 动态点赞 | POST | `/api/social/posts/{postId}/like` | 点赞 |
| 群组 | GET/POST | `/api/groups/list`、`/api/groups/{groupId}/join` | 兴趣群组浏览与加入 |
| 用药提醒 | GET/POST | `/api/medication/list/{userId}`、`/api/medication/add` | 提醒列表与新增 |
| 地址管理 | GET/POST | `/api/address/list/{userId}`、`/api/address/add` | 收货地址 |
| 意见反馈 | POST | `/api/feedback/add` | 提交反馈 |
| 模拟支付 | POST | `/api/pay/mock/success` | 本地调试时模拟支付成功 |

### 请求 / 返回示例

管理端登录：

```http
POST /admin/auth/login
Content-Type: application/json

{
  "username": "wxh",
  "password": "123456"
}
```

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userInfo": {
      "id": 1,
      "username": "wxh",
      "name": "管理员",
      "role": "admin"
    }
  }
}
```

健康数据录入：

```json
POST /api/health/add

{
  "userId": 1,
  "bloodPressureHigh": 128,
  "bloodPressureLow": 82,
  "heartRate": 76,
  "bloodSugar": 5.6,
  "weight": 62.5
}
```

## 七、常见问题（FAQ）

**1. 启动后端报 `Access denied for user 'root'@'localhost'`**
数据库密码与 `application.yml` 不一致。确认本机 MySQL 实际密码后修改配置文件；也可临时用环境变量覆盖：`$env:SPRING_DATASOURCE_PASSWORD='你的密码'` 后再执行 `mvn spring-boot:run`。

**2. 启动报 `Table 'elderly_care.news_article' doesn't exist` 或 `Unknown column 'category'`**
建表脚本未执行完整，或导入了旧版本脚本导致字段不一致。按「运行步骤」顺序重新导入脚本；已存在的表不会被覆盖，缺失字段需用 `ALTER TABLE ... ADD COLUMN` 补齐。

**3. Maven 报 `AccessDeniedException: D:\...\mvn_repo\...`**
本地仓库目录无写权限（或被沙箱限制）。给 Maven 仓库目录授权，或在仓库外可写磁盘上重新指定 `localRepository`。

**4. 编译报 `Error reading old mojo status ... inputFiles.lst: Input length = 1`**
旧编译缓存文件编码损坏（常见于中文项目路径）。删除 `后端/target/maven-status` 后重新编译即可。

**5. 用 `mysql -e "source xxx.sql"` 导入失败，提示 `Failed to open file`**
`source` 对中文路径支持不佳。把 SQL 文件复制到纯英文目录（如 `C:\temp\sql`）再导入。

**6. 小程序请求全部失败**
`app.js` 的 `baseUrl` 仍指向线上/临时隧道地址。改成本机后端地址，并在开发者工具中关闭域名校验。

**7. 管理后台能打开但接口 404 / 500**
先确认后端 8080 已启动；404 多为路径写错（如 `/api/news/list` 不存在，应为 `/api/news/page`），500 多为数据库表或字段缺失，查看后端控制台异常栈定位。

**8. 文件上传失败**
`application.yml` 中 `file.upload-path` 指向 `d:/养老/upload/`，需确保该目录存在且有写权限，或改为项目内 `upload/` 目录。

## 八、TODO / 未来计划

- [ ] 接入真实微信支付（当前为模拟支付回调）
- [ ] 引入 Redis 缓存热点数据与登录态，替换内存限流
- [ ] 接入微信订阅消息，实现用药提醒、紧急呼叫的实时推送
- [ ] 补充 Swagger / OpenAPI 文档，自动生成接口说明
- [ ] 编写单元测试与接口自动化测试，接入 CI
- [ ] 增加 Docker Compose 一键部署（MySQL + 后端 + Nginx）
- [ ] 管理后台增加数据导出（Excel）与细粒度权限控制
- [ ] 小程序端增加语音输入与无障碍适老化改造
- [ ] 健康数据增加可视化趋势图与异常预警规则
