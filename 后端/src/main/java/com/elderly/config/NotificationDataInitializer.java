package com.elderly.config;

import com.elderly.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class NotificationDataInitializer implements CommandLineRunner {
    
    @Autowired
    private NotificationService notificationService;
    
    @Override
    public void run(String... args) throws Exception {
        initNotifications();
    }
    
    private void initNotifications() {
        try {
            long count = notificationService.count();
            if (count == 0) {
                notificationService.sendNotification(1L, "欢迎使用智慧养老平台", "尊敬的用户，欢迎您使用智慧养老服务平台！我们致力于为您提供便捷、贴心的养老服务。如有任何问题，请随时联系客服。", 0);
                notificationService.sendNotification(1L, "服务订单已确认", "您的居家保洁服务订单已确认，服务人员将于明天上午9点上门服务，请保持电话畅通。", 1);
                notificationService.sendNotification(1L, "用药提醒", "温馨提醒：请在今天下午2点服用降压药，请勿忘记。", 2);
                System.out.println("通知测试数据初始化完成");
            }
        } catch (Exception e) {
            System.out.println("通知数据初始化跳过: " + e.getMessage());
        }
    }
}
