package com.elderly.controller;

import com.elderly.common.Result;
import com.elderly.entity.Notification;
import com.elderly.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    @GetMapping("/list/{userId}")
    public Result<List<Notification>> getList(@PathVariable Long userId,
                                               @RequestParam(required = false) Integer type) {
        List<Notification> list;
        if (type != null) {
            list = notificationService.getListByUserIdAndType(userId, type);
        } else {
            list = notificationService.getListByUserId(userId);
        }
        return Result.success(list);
    }
    
    @GetMapping("/unread/{userId}")
    public Result<Map<String, Integer>> getUnreadCount(@PathVariable Long userId) {
        int count = notificationService.getUnreadCount(userId);
        Map<String, Integer> result = new HashMap<>();
        result.put("count", count);
        return Result.success(result);
    }
    
    @PutMapping("/read/{id}")
    public Result<Boolean> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return Result.success();
    }
    
    @PutMapping("/readAll/{userId}")
    public Result<Boolean> markAllAsRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return Result.success();
    }
    
    @GetMapping("/detail/{id}")
    public Result<Notification> getDetail(@PathVariable Long id) {
        Notification notification = notificationService.getById(id);
        if (notification != null && notification.getIsRead() == 0) {
            notificationService.markAsRead(id);
            notification.setIsRead(1);
        }
        return Result.success(notification);
    }
    
    @PostMapping("/send")
    public Result<Boolean> sendNotification(@RequestBody Notification notification) {
        notificationService.sendNotification(
            notification.getUserId(),
            notification.getTitle(),
            notification.getContent(),
            notification.getType(),
            notification.getExtraData()
        );
        return Result.success();
    }
}
