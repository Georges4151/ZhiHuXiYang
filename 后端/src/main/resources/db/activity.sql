CREATE TABLE IF NOT EXISTS activity (
    id BIGINT NOT NULL AUTO_INCREMENT,
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
    organizer_name VARCHAR(100),
    contact_phone VARCHAR(20),
    tips TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS activity_comment (
    id BIGINT NOT NULL AUTO_INCREMENT,
    activity_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    user_name VARCHAR(100),
    content TEXT NOT NULL,
    parent_id BIGINT DEFAULT 0,
    reply_to_user_id BIGINT,
    reply_to_user_name VARCHAR(100),
    likes INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_activity_id (activity_id),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO activity (title, description, category, location, start_time, end_time, registration_deadline, max_participants, current_participants, likes, organizer_name, contact_phone, tips) VALUES
('春季健康养生讲座', '邀请知名中医专家讲解春季养生知识，包括饮食调理、运动保健等内容。', 1, '社区活动中心一楼会议室', '2024-03-15 09:00:00', '2024-03-15 11:00:00', '2024-03-14 18:00:00', 50, 35, 28, '社区服务中心', '12345678', '请提前10分钟到场签到'),
('太极拳培训班', '专业太极拳教练授课，适合老年人学习，强身健体。', 2, '社区健身广场', '2024-03-16 07:00:00', '2024-03-16 08:30:00', '2024-03-15 18:00:00', 30, 22, 15, '文体活动中心', '12345679', '请穿着宽松舒适的运动服装'),
('关爱老人志愿服务活动', '志愿者上门为独居老人提供生活帮助和心理慰藉。', 3, '社区各小区', '2024-03-17 09:00:00', '2024-03-17 17:00:00', '2024-03-16 18:00:00', 20, 18, 32, '志愿者协会', '12345680', '志愿者需提前报名并参加培训'),
('书法兴趣班', '学习传统书法艺术，陶冶情操，丰富晚年生活。', 4, '社区文化活动室', '2024-03-18 14:00:00', '2024-03-18 16:00:00', '2024-03-17 18:00:00', 25, 20, 12, '老年大学', '12345681', '笔墨纸砚由社区提供');
