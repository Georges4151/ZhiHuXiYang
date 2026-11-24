package com.elderly.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.entity.Notification;
import com.elderly.mapper.NotificationMapper;
import com.elderly.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {
    
    @Autowired
    private NotificationMapper notificationMapper;
    
    @Override
    public List<Notification> getListByUserId(Long userId) {
        return notificationMapper.selectRecentByUserId(userId, 50);
    }
    
    @Override
    public List<Notification> getListByUserIdAndType(Long userId, Integer type) {
        return notificationMapper.selectByUserIdAndType(userId, type);
    }
    
    @Override
    public int getUnreadCount(Long userId) {
        return notificationMapper.countUnread(userId);
    }
    
    @Override
    public void markAsRead(Long id) {
        Notification notification = getById(id);
        if (notification != null) {
            notification.setIsRead(1);
            updateById(notification);
        }
    }
    
    @Override
    public void markAllAsRead(Long userId) {
        notificationMapper.markAllRead(userId);
    }
    
    @Override
    public void sendNotification(Long userId, String title, String content, Integer type) {
        sendNotification(userId, title, content, type, null);
    }
    
    @Override
    public void sendNotification(Long userId, String title, String content, Integer type, String extraData) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setIsRead(0);
        notification.setExtraData(extraData);
        save(notification);
    }
}
