USE elderly_care;

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

CREATE TABLE IF NOT EXISTS service_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    price DECIMAL(10,2) DEFAULT 0,
    image VARCHAR(500),
    category INT DEFAULT 0,
    duration INT DEFAULT 60,
    sales INT DEFAULT 0,
    rating DECIMAL(2,1) DEFAULT 4.8,
    icon VARCHAR(20),
    status INT DEFAULT 1,
    sort INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    INDEX idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS service_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(50) NOT NULL,
    user_id BIGINT NOT NULL,
    service_item_id BIGINT,
    service_name VARCHAR(100),
    price DECIMAL(10,2),
    status INT DEFAULT 0,
    service_time DATETIME,
    address VARCHAR(200),
    contact_name VARCHAR(50),
    contact_phone VARCHAR(20),
    worker_id BIGINT,
    worker_name VARCHAR(50),
    worker_phone VARCHAR(20),
    service_rating INT,
    service_comment VARCHAR(500),
    pay_status INT DEFAULT 0,
    pay_time VARCHAR(50),
    remark VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    INDEX idx_user_id (user_id),
    INDEX idx_order_no (order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS family_relation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    family_user_id BIGINT,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    relation VARCHAR(20),
    is_primary INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO news_article (title, content, summary, category, author, view_count, is_top, publish_time) VALUES
('2024年养老金调整方案公布', '近日，人社部公布了2024年养老金调整方案，全国总体调整水平为2023年退休人员月人均基本养老金的3.8%。此次调整采取定额调整、挂钩调整与适当倾斜相结合的办法，确保退休人员基本生活水平稳步提高。', '全国总体调整水平为3.8%', 1, '养老资讯', 1256, 1, NOW()),
('老年人健康饮食指南', '合理的饮食对老年人的健康至关重要。建议老年人多吃蔬菜水果，少吃油腻食物，保持营养均衡。每日三餐要规律，早餐要吃好，午餐要吃饱，晚餐要吃少。', '合理饮食对老年人健康至关重要', 2, '健康顾问', 892, 0, NOW()),
('社区养老服务站开放通知', '我社区养老服务站已于近日正式开放，欢迎广大老年朋友前来体验各项服务。服务站提供日间照料、康复理疗、文化娱乐等多种服务。', '欢迎广大老年朋友前来体验', 3, '社区动态', 567, 0, NOW()),
('医保报销政策解读', '本文详细解读老年人医保报销政策，帮助您了解如何享受医保优惠。包括门诊报销、住院报销、慢病报销等多种情况。', '了解医保报销政策', 1, '政策解读', 1024, 0, NOW()),
('冬季养生小贴士', '冬季是感冒高发季节，老年人更需要注意预防。以下是一些实用的养生小贴士，帮助您健康过冬。', '冬季养生知识', 2, '健康顾问', 756, 0, NOW());

INSERT INTO service_item (name, description, price, category, duration, sales, rating, icon) VALUES
('居家保洁', '专业保洁人员上门提供家庭清洁服务，让您的家焕然一新', 80.00, 1, 120, 1256, 4.9, '🧹'),
('助餐服务', '为老年人提供营养均衡的餐饮服务，荤素搭配', 20.00, 2, 60, 3562, 4.8, '🍽️'),
('康复护理', '专业康复师提供康复训练指导，帮助恢复身体机能', 150.00, 3, 90, 892, 4.9, '🏥'),
('陪同就医', '专人陪同老人前往医院就诊，全程贴心服务', 100.00, 4, 180, 1567, 4.7, '👴'),
('心理慰藉', '专业心理咨询师提供心理疏导服务，关爱心理健康', 120.00, 3, 60, 456, 4.8, '💬'),
('代购跑腿', '帮您购买生活用品、药品等，送货上门', 30.00, 1, 60, 2891, 4.6, '🏃');

CREATE TABLE IF NOT EXISTS notification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    content TEXT,
    type INT DEFAULT 0 COMMENT '0系统 1服务 2健康 3活动',
    is_read INT DEFAULT 0,
    extra_data VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    INDEX idx_user_id (user_id),
    INDEX idx_type (type),
    INDEX idx_is_read (is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO notification (user_id, title, content, type, is_read) VALUES
(1, '欢迎使用智慧养老平台', '尊敬的用户，欢迎您使用智慧养老服务平台！我们致力于为您提供便捷、贴心的养老服务。如有任何问题，请随时联系客服。', 0, 0),
(1, '服务订单已确认', '您的"居家保洁"服务订单已确认，服务人员将于明天上午9点上门服务，请保持电话畅通。', 1, 0),
(1, '用药提醒', '温馨提醒：请在今天下午2点服用降压药，请勿忘记。', 2, 0);
