-- 在MySQL中执行以下SQL创建健康数据表

USE elderly_care;

-- 健康数据表
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

-- 用药提醒表
CREATE TABLE IF NOT EXISTS medication_reminder (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    dosage VARCHAR(100),
    reminder_time TIME NOT NULL,
    status INT DEFAULT 0,
    remark VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
