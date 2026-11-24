USE elderly_care;

CREATE TABLE IF NOT EXISTS activity (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    category INT DEFAULT 1,
    cover_image VARCHAR(500),
    location VARCHAR(200),
    start_time DATETIME,
    end_time DATETIME,
    registration_deadline DATETIME,
    max_participants INT DEFAULT 50,
    current_participants INT DEFAULT 0,
    likes INT DEFAULT 0,
    organizer_id BIGINT,
    organizer_name VARCHAR(50),
    contact_phone VARCHAR(20),
    tips VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    INDEX idx_category (category),
    INDEX idx_start_time (start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS news_article (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    summary VARCHAR(500),
    category INT DEFAULT 0,
    cover_image VARCHAR(500),
    author VARCHAR(50),
    view_count INT DEFAULT 0,
    is_top INT DEFAULT 0,
    status INT DEFAULT 1,
    publish_time DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    INDEX idx_category (category),
    INDEX idx_publish_time (publish_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS health_data (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    blood_pressure_high DECIMAL(5,1),
    blood_pressure_low DECIMAL(5,1),
    heart_rate DECIMAL(5,1),
    blood_sugar DECIMAL(5,1),
    temperature DECIMAL(4,1),
    weight DECIMAL(5,1),
    data_source INT DEFAULT 1,
    device_id VARCHAR(100),
    measure_time DATETIME,
    remark VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    INDEX idx_user_id (user_id),
    INDEX idx_measure_time (measure_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS emergency_call (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    user_name VARCHAR(50),
    user_phone VARCHAR(20),
    latitude DOUBLE,
    longitude DOUBLE,
    address VARCHAR(200),
    call_type INT DEFAULT 1,
    call_status INT DEFAULT 0,
    response_time DATETIME,
    responder_id BIGINT,
    responder_name VARCHAR(50),
    responder_phone VARCHAR(20),
    remark VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    INDEX idx_user_id (user_id),
    INDEX idx_call_status (call_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS feedback (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    contact VARCHAR(50),
    status INT DEFAULT 0,
    reply TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO activity (title, description, category, location, start_time, end_time, max_participants, contact_phone) VALUES
('健康讲座：冬季养生保健', '邀请专业医师讲解冬季养生知识，欢迎参加', 1, '黄骅市社区服务中心一楼', DATE_ADD(NOW(), INTERVAL 3 DAY), DATE_ADD(NOW(), INTERVAL 3 DAY) + INTERVAL 2 HOUR, 50, '0317-8888888'),
('书法兴趣班', '每周三下午书法学习交流', 4, '社区活动中心', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 1 DAY) + INTERVAL 2 HOUR, 20, '0317-8888888'),
('志愿服务：关爱独居老人', '志愿者上门探访独居老人', 3, '黄骅市各社区', DATE_ADD(NOW(), INTERVAL 5 DAY), DATE_ADD(NOW(), INTERVAL 5 DAY) + INTERVAL 4 HOUR, 30, '0317-8888888');

INSERT INTO news_article (title, content, summary, category, author, view_count, status) VALUES
('2024年养老金调整方案公布', '近日，人社部公布了2024年养老金调整方案，全国总体调整水平为2023年退休人员月人均基本养老金的3.8%。', '全国总体调整水平为3.8%', 1, '养老资讯', 1256, 1),
('老年人健康饮食指南', '合理的饮食对老年人的健康至关重要。建议老年人多吃蔬菜水果，少吃油腻食物。', '合理饮食对老年人健康至关重要', 2, '健康顾问', 892, 1);

INSERT INTO health_data (user_id, blood_pressure_high, blood_pressure_low, heart_rate, blood_sugar, measure_time) VALUES
(1, 130.0, 85.0, 72.0, 5.6, NOW()),
(1, 128.0, 82.0, 75.0, 5.8, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(1, 135.0, 88.0, 78.0, 6.2, DATE_SUB(NOW(), INTERVAL 2 DAY));

INSERT INTO emergency_call (user_id, user_name, user_phone, address, call_type, call_status) VALUES
(1, '测试用户', '13800138000', '河北省沧州市黄骅市', 1, 0);

INSERT INTO feedback (user_id, content, contact, status) VALUES
(1, '希望增加更多健康讲座活动', '13800138000', 0);
