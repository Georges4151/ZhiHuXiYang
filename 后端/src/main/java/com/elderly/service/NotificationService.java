package com.elderly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.entity.Notification;

import java.util.List;

public interface NotificationService extends IService<Notification> {
    
    List<Notification> getListByUserId(Long userId);
    
    List<Notification> getListByUserIdAndType(Long userId, Integer type);
    
    int getUnreadCount(Long userId);
    
    void markAsRead(Long id);
    
    void markAllAsRead(Long userId);
    
    void sendNotification(Long userId, String title, String content, Integer type);
    
    void sendNotification(Long userId, String title, String content, Integer type, String extraData);
}
