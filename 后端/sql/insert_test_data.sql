USE elderly_care;

INSERT INTO activity (title, description, category, location, start_time, end_time, max_participants, contact_phone) VALUES
('Health Lecture', 'Winter health tips', 1, 'Community Center', DATE_ADD(NOW(), INTERVAL 3 DAY), DATE_ADD(NOW(), INTERVAL 3 DAY) + INTERVAL 2 HOUR, 50, '0317-8888888'),
('Calligraphy Class', 'Weekly calligraphy', 4, 'Activity Center', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 1 DAY) + INTERVAL 2 HOUR, 20, '0317-8888888');

INSERT INTO news_article (title, content, summary, category, author, view_count, status) VALUES
('Pension Policy Update', '2024 pension adjustment announced', '3.8% increase', 1, 'Admin', 1256, 1),
('Health Guide', 'Healthy diet for elderly', 'Diet tips', 2, 'Doctor', 892, 1);

INSERT INTO health_data (user_id, blood_pressure_high, blood_pressure_low, heart_rate, blood_sugar, measure_time) VALUES
(1, 130.0, 85.0, 72.0, 5.6, NOW()),
(1, 128.0, 82.0, 75.0, 5.8, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(1, 135.0, 88.0, 78.0, 6.2, DATE_SUB(NOW(), INTERVAL 2 DAY));

INSERT INTO emergency_call (user_id, user_name, user_phone, address, call_type, call_status) VALUES
(1, 'Test User', '13800138000', 'Huanghua City', 1, 0);

INSERT INTO feedback (user_id, content, contact, status) VALUES
(1, 'Hope for more health lectures', '13800138000', 0);
