package com.elderly.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Override
    public void run(String... args) throws Exception {
        initNewsData();
        initServiceData();
    }
    
    private void initNewsData() {
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM news_article", Integer.class);
        
        if (count == null || count == 0) {
            jdbcTemplate.update("INSERT INTO news_article (title, content, summary, category, author, view_count, is_top, status, publish_time) VALUES ('2024年养老金调整方案公布', '近日，人社部公布了2024年养老金调整方案，全国总体调整水平为2023年退休人员月人均基本养老金的3.8%。', '养老金调整方案公布，涨幅3.8%', 1, '管理员', 1256, 1, 1, NOW())");
            jdbcTemplate.update("INSERT INTO news_article (title, content, summary, category, author, view_count, is_top, status, publish_time) VALUES ('老年人健康饮食指南', '合理的饮食对老年人的健康至关重要。建议老年人多吃蔬菜水果，少吃油腻食物，保持营养均衡。', '老年人健康饮食建议', 2, '健康管理师', 892, 0, 1, NOW())");
            jdbcTemplate.update("INSERT INTO news_article (title, content, summary, category, author, view_count, is_top, status, publish_time) VALUES ('社区养老服务站开放通知', '我社区养老服务站已于近日正式开放，欢迎广大老年朋友前来体验各项服务。', '社区养老服务站开放', 3, '社区管理员', 567, 0, 1, NOW())");
            System.out.println("新闻数据初始化完成");
        }
    }
    
    private void initServiceData() {
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM service_item", Integer.class);
        
        if (count == null || count == 0) {
            jdbcTemplate.update("INSERT INTO service_item (name, description, price, category, duration, sales, rating, icon, status, sort) VALUES ('居家保洁', '专业保洁人员上门提供家庭清洁服务', 80.00, 1, 120, 1256, 4.9, '🧹', 1, 1)");
            jdbcTemplate.update("INSERT INTO service_item (name, description, price, category, duration, sales, rating, icon, status, sort) VALUES ('助餐服务', '为老年人提供营养均衡的餐饮服务', 20.00, 2, 60, 3562, 4.8, '🍽️', 1, 2)");
            jdbcTemplate.update("INSERT INTO service_item (name, description, price, category, duration, sales, rating, icon, status, sort) VALUES ('康复护理', '专业康复师提供康复训练指导', 150.00, 3, 90, 892, 4.9, '🏥', 1, 3)");
            jdbcTemplate.update("INSERT INTO service_item (name, description, price, category, duration, sales, rating, icon, status, sort) VALUES ('陪同就医', '专人陪同老人前往医院就诊', 100.00, 4, 180, 1567, 4.7, '👴', 1, 4)");
            jdbcTemplate.update("INSERT INTO service_item (name, description, price, category, duration, sales, rating, icon, status, sort) VALUES ('心理慰藉', '专业心理咨询师提供心理疏导服务', 120.00, 3, 60, 456, 4.8, '💬', 1, 5)");
            jdbcTemplate.update("INSERT INTO service_item (name, description, price, category, duration, sales, rating, icon, status, sort) VALUES ('代购跑腿', '帮您购买生活用品、药品等', 30.00, 1, 60, 2891, 4.6, '🏃', 1, 6)");
            System.out.println("服务数据初始化完成");
        }
    }
}
