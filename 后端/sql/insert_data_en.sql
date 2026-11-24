USE elderly_care;

INSERT INTO news_article (title, content, summary, category, author, view_count, is_top, publish_time) VALUES
('2024 Pension Adjustment Plan Announced', 'The Ministry of Human Resources and Social Security announced the 2024 pension adjustment plan, with a national overall adjustment level of 3.8%.', 'National adjustment level 3.8%', 1, 'Pension News', 1256, 1, NOW()),
('Healthy Diet Guide for Elderly', 'A reasonable diet is crucial for the health of the elderly. It is recommended to eat more vegetables and fruits.', 'Healthy diet is important', 2, 'Health Advisor', 892, 0, NOW()),
('Community Service Station Opening Notice', 'Our community elderly service station has officially opened. Welcome to experience our services.', 'Welcome to visit', 3, 'Community News', 567, 0, NOW());

INSERT INTO service_item (name, description, price, category, duration, sales, rating, icon) VALUES
('Home Cleaning', 'Professional cleaning service at your home', 80.00, 1, 120, 1256, 4.9, 'house'),
('Meal Service', 'Nutritious meal service for elderly', 20.00, 2, 60, 3562, 4.8, 'food'),
('Rehabilitation Care', 'Professional rehabilitation training guidance', 150.00, 3, 90, 892, 4.9, 'hospital'),
('Medical Escort', 'Accompany elderly to hospital visits', 100.00, 4, 180, 1567, 4.7, 'person'),
('Psychological Comfort', 'Professional psychological counseling service', 120.00, 3, 60, 456, 4.8, 'chat'),
('Shopping Service', 'Help buy daily necessities and medicines', 30.00, 1, 60, 2891, 4.6, 'run');
