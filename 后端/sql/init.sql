-- 创建数据库
CREATE DATABASE IF NOT EXISTS elderly_care DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE elderly_care;

-- 用户表
CREATE TABLE IF NOT EXISTS elderly_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    open_id VARCHAR(100) COMMENT '微信OpenID',
    name VARCHAR(50) COMMENT '姓名',
    phone VARCHAR(20) COMMENT '手机号',
    password VARCHAR(100) COMMENT '密码',
    avatar VARCHAR(500) COMMENT '头像',
    gender INT DEFAULT 0 COMMENT '性别 0未知 1男 2女',
    birthday DATE COMMENT '出生日期',
    id_card VARCHAR(20) COMMENT '身份证号',
    address VARCHAR(200) COMMENT '详细地址',
    latitude DOUBLE COMMENT '纬度',
    longitude DOUBLE COMMENT '经度',
    emergency_contact VARCHAR(50) COMMENT '紧急联系人',
    emergency_phone VARCHAR(20) COMMENT '紧急联系电话',
    user_type INT DEFAULT 1 COMMENT '用户类型 1老人 2家属 3服务人员',
    health_condition VARCHAR(500) COMMENT '健康状况',
    status INT DEFAULT 1 COMMENT '状态 0禁用 1正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '是否删除 0否 1是',
    INDEX idx_open_id (open_id),
    INDEX idx_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 咨询记录表
CREATE TABLE IF NOT EXISTS consultation_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT COMMENT '用户ID',
    question TEXT COMMENT '问题内容',
    answer TEXT COMMENT '回答内容',
    consultation_type INT DEFAULT 1 COMMENT '咨询类型 1文字 2语音',
    voice_url VARCHAR(500) COMMENT '语音文件URL',
    session_id VARCHAR(100) COMMENT '会话ID',
    feedback INT COMMENT '反馈评价 1好评 2中评 3差评',
    feedback_content VARCHAR(500) COMMENT '反馈内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '是否删除',
    INDEX idx_user_id (user_id),
    INDEX idx_session_id (session_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='咨询记录表';

-- 健康数据表
CREATE TABLE IF NOT EXISTS health_data (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT COMMENT '用户ID',
    blood_pressure_high DECIMAL(5,2) COMMENT '收缩压',
    blood_pressure_low DECIMAL(5,2) COMMENT '舒张压',
    heart_rate DECIMAL(5,2) COMMENT '心率',
    blood_sugar DECIMAL(5,2) COMMENT '血糖',
    temperature DECIMAL(4,1) COMMENT '体温',
    weight DECIMAL(5,2) COMMENT '体重',
    data_source INT DEFAULT 1 COMMENT '数据来源 1手动录入 2设备采集',
    device_id VARCHAR(100) COMMENT '设备ID',
    measure_time DATETIME COMMENT '测量时间',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '是否删除',
    INDEX idx_user_id (user_id),
    INDEX idx_measure_time (measure_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康数据表';

-- 用药提醒表
CREATE TABLE IF NOT EXISTS medication_reminder (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT COMMENT '用户ID',
    medication_name VARCHAR(100) COMMENT '药品名称',
    dosage VARCHAR(50) COMMENT '剂量',
    frequency VARCHAR(50) COMMENT '服用频率',
    reminder_time DATETIME COMMENT '提醒时间',
    start_date DATE COMMENT '开始日期',
    end_date DATE COMMENT '结束日期',
    status INT DEFAULT 1 COMMENT '状态 0停用 1启用',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '是否删除',
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用药提醒表';

-- 服务项目表
CREATE TABLE IF NOT EXISTS service_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(100) COMMENT '服务名称',
    description VARCHAR(500) COMMENT '服务描述',
    price DECIMAL(10,2) COMMENT '服务价格',
    image VARCHAR(500) COMMENT '服务图片',
    category_id INT COMMENT '分类ID',
    category_name VARCHAR(50) COMMENT '分类名称',
    service_duration INT COMMENT '服务时长(分钟)',
    status INT DEFAULT 1 COMMENT '状态 0下架 1上架',
    sort INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '是否删除',
    INDEX idx_category_id (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务项目表';

-- 服务订单表
CREATE TABLE IF NOT EXISTS service_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    order_no VARCHAR(50) COMMENT '订单编号',
    user_id BIGINT COMMENT '用户ID',
    service_id BIGINT COMMENT '服务ID',
    service_name VARCHAR(100) COMMENT '服务名称',
    amount DECIMAL(10,2) COMMENT '订单金额',
    order_status INT DEFAULT 0 COMMENT '订单状态 0待接单 1已接单 2服务中 3已完成 4已取消',
    service_address VARCHAR(200) COMMENT '服务地址',
    contact_name VARCHAR(50) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    appointment_time VARCHAR(50) COMMENT '预约时间',
    worker_id BIGINT COMMENT '服务人员ID',
    worker_name VARCHAR(50) COMMENT '服务人员姓名',
    worker_phone VARCHAR(20) COMMENT '服务人员电话',
    service_rating INT COMMENT '服务评分',
    service_comment VARCHAR(500) COMMENT '服务评价',
    pay_status INT DEFAULT 0 COMMENT '支付状态 0未支付 1已支付',
    pay_time VARCHAR(50) COMMENT '支付时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '是否删除',
    INDEX idx_order_no (order_no),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务订单表';

-- 紧急呼叫表
CREATE TABLE IF NOT EXISTS emergency_call (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT COMMENT '用户ID',
    user_name VARCHAR(50) COMMENT '用户姓名',
    user_phone VARCHAR(20) COMMENT '用户电话',
    latitude DOUBLE COMMENT '纬度',
    longitude DOUBLE COMMENT '经度',
    address VARCHAR(200) COMMENT '地址',
    call_type INT DEFAULT 1 COMMENT '呼叫类型 1紧急呼叫 2健康求助',
    call_status INT DEFAULT 0 COMMENT '呼叫状态 0待处理 1处理中 2已解决',
    response_time DATETIME COMMENT '响应时间',
    responder_id BIGINT COMMENT '响应人ID',
    responder_name VARCHAR(50) COMMENT '响应人姓名',
    responder_phone VARCHAR(20) COMMENT '响应人电话',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '是否删除',
    INDEX idx_user_id (user_id),
    INDEX idx_call_status (call_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='紧急呼叫表';

-- 家属关系表
CREATE TABLE IF NOT EXISTS family_relation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    elderly_id BIGINT COMMENT '老人ID',
    family_id BIGINT COMMENT '家属ID',
    relation_type VARCHAR(20) COMMENT '关系类型',
    status INT DEFAULT 1 COMMENT '状态 0解除 1正常',
    elderly_name VARCHAR(50) COMMENT '老人姓名',
    family_name VARCHAR(50) COMMENT '家属姓名',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '是否删除',
    INDEX idx_elderly_id (elderly_id),
    INDEX idx_family_id (family_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='家属关系表';

-- 资讯文章表
CREATE TABLE IF NOT EXISTS news_article (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    title VARCHAR(200) COMMENT '标题',
    content TEXT COMMENT '内容',
    summary VARCHAR(500) COMMENT '摘要',
    cover_image VARCHAR(500) COMMENT '封面图片',
    category_id INT COMMENT '分类ID',
    category_name VARCHAR(50) COMMENT '分类名称',
    author VARCHAR(50) COMMENT '作者',
    view_type INT DEFAULT 1 COMMENT '查看类型 1全部 2登录可见',
    read_count INT DEFAULT 0 COMMENT '阅读数',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    status INT DEFAULT 1 COMMENT '状态 0草稿 1发布',
    is_top INT DEFAULT 0 COMMENT '是否置顶 0否 1是',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '是否删除',
    INDEX idx_category_id (category_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资讯文章表';

-- 社区活动表
CREATE TABLE IF NOT EXISTS community_activity (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    title VARCHAR(200) COMMENT '活动标题',
    content TEXT COMMENT '活动内容',
    cover_image VARCHAR(500) COMMENT '封面图片',
    activity_time DATETIME COMMENT '活动时间',
    activity_address VARCHAR(200) COMMENT '活动地点',
    max_participants INT COMMENT '最大参与人数',
    current_participants INT DEFAULT 0 COMMENT '当前参与人数',
    status INT DEFAULT 1 COMMENT '状态 0取消 1报名中 2已结束',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '是否删除',
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社区活动表';

-- 活动报名表
CREATE TABLE IF NOT EXISTS activity_registration (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    activity_id BIGINT COMMENT '活动ID',
    user_id BIGINT COMMENT '用户ID',
    user_name VARCHAR(50) COMMENT '用户姓名',
    user_phone VARCHAR(20) COMMENT '用户电话',
    status INT DEFAULT 1 COMMENT '状态 0取消 1已报名',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '是否删除',
    INDEX idx_activity_id (activity_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动报名表';

-- 插入测试数据
INSERT INTO service_item (name, description, price, category_id, category_name, service_duration, status, sort) VALUES
('居家保洁', '专业保洁人员上门提供家庭清洁服务', 80.00, 1, '家政服务', 120, 1, 1),
('助餐服务', '为老年人提供营养均衡的餐饮服务', 20.00, 2, '餐饮服务', 60, 1, 2),
('康复护理', '专业康复师提供康复训练指导', 150.00, 3, '医疗护理', 90, 1, 3),
('陪同就医', '专人陪同老人前往医院就诊', 100.00, 4, '陪护服务', 180, 1, 4),
('心理慰藉', '专业心理咨询师提供心理疏导服务', 120.00, 5, '心理服务', 60, 1, 5),
('法律咨询', '为老年人提供法律咨询服务', 50.00, 6, '咨询服务', 60, 1, 6);

INSERT INTO news_article (title, content, summary, category_id, category_name, author, status, is_top) VALUES
('2024年养老金调整方案公布', '近日，人社部公布了2024年养老金调整方案，全国总体调整水平为2023年退休人员月人均基本养老金的3.8%。', '养老金调整方案公布，涨幅3.8%', 1, '政策法规', '管理员', 1, 1),
('老年人健康饮食指南', '合理的饮食对老年人的健康至关重要。建议老年人多吃蔬菜水果，少吃油腻食物，保持营养均衡。', '老年人健康饮食建议', 2, '健康知识', '健康管理师', 1, 0),
('社区养老服务站开放通知', '我社区养老服务站已于近日正式开放，欢迎广大老年朋友前来体验各项服务。', '社区养老服务站开放', 3, '社区动态', '社区管理员', 1, 0);
