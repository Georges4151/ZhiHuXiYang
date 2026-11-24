USE elderly_care;
ALTER TABLE elderly_user ADD COLUMN password VARCHAR(100) COMMENT '密码' AFTER phone;
