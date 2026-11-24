package com.elderly.controller;

import com.elderly.common.Result;
import com.elderly.entity.FamilyNotification;
import com.elderly.entity.ServiceOrder;
import com.elderly.service.FamilyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/family")
public class FamilyController {
    
    @Autowired
    private FamilyService familyService;
    
    // ==================== 家属绑定 ====================
    
    @PostMapping("/bind")
    public Result<Boolean> bindFamily(
            @RequestParam Long userId,
            @RequestParam Long familyUserId,
            @RequestParam String name,
            @RequestParam String phone,
            @RequestParam String relation) {
        boolean success = familyService.bindFamily(userId, familyUserId, name, phone, relation);
        return success ? Result.success(true) : Result.error("绑定失败");
    }
    
    @PostMapping("/addManual")
    public Result<Boolean> addFamilyManual(@RequestBody Map<String, Object> params) {
        Long userId = params.get("userId") != null ? Long.valueOf(params.get("userId").toString()) : null;
        String name = (String) params.get("name");
        String phone = (String) params.get("phone");
        String relation = (String) params.get("relation");
        
        if (userId == null || name == null || phone == null || relation == null) {
            return Result.error("参数不完整");
        }
        
        boolean success = familyService.addFamilyManual(userId, name, phone, relation);
        return success ? Result.success(true) : Result.error("添加失败");
    }
    
    @PostMapping("/unbind/{relationId}")
    public Result<Boolean> unbindFamily(@PathVariable Long relationId) {
        boolean success = familyService.unbindFamily(relationId);
        return success ? Result.success(true) : Result.error("解绑失败");
    }
    
    @GetMapping("/members/{userId}")
    public Result<List<Map<String, Object>>> getFamilyMembers(@PathVariable Long userId) {
        List<Map<String, Object>> members = familyService.getFamilyMembers(userId);
        return Result.success(members);
    }
    
    @GetMapping("/bind-users/{familyUserId}")
    public Result<List<Map<String, Object>>> getBindUsers(@PathVariable Long familyUserId) {
        List<Map<String, Object>> users = familyService.getBindUsers(familyUserId);
        return Result.success(users);
    }
    
    // ==================== 健康数据查看 ====================
    
    @GetMapping("/health-data/{userId}")
    public Result<List<Map<String, Object>>> getFamilyHealthData(
            @PathVariable Long userId,
            @RequestParam Long familyUserId,
            @RequestParam(required = false) String dataType,
            @RequestParam(required = false, defaultValue = "7") Integer days) {
        List<Map<String, Object>> healthData = familyService.getFamilyHealthData(userId, familyUserId, dataType, days);
        return Result.success(healthData);
    }
    
    @GetMapping("/health-stats/{userId}")
    public Result<Map<String, Object>> getFamilyHealthStats(
            @PathVariable Long userId,
            @RequestParam Long familyUserId) {
        Map<String, Object> stats = familyService.getFamilyHealthStats(userId, familyUserId);
        return Result.success(stats);
    }
    
    // ==================== 服务进度跟踪 ====================
    
    @GetMapping("/service-orders/{userId}")
    public Result<List<Map<String, Object>>> getFamilyServiceOrders(
            @PathVariable Long userId,
            @RequestParam Long familyUserId,
            @RequestParam(required = false) Integer status) {
        List<Map<String, Object>> orders = familyService.getFamilyServiceOrders(userId, familyUserId, status);
        return Result.success(orders);
    }
    
    @GetMapping("/service-order/{orderId}")
    public Result<ServiceOrder> getFamilyServiceOrderDetail(
            @PathVariable Long orderId,
            @RequestParam Long familyUserId) {
        ServiceOrder order = familyService.getFamilyServiceOrderDetail(orderId, familyUserId);
        if (order == null) {
            return Result.error("订单不存在或无权限查看");
        }
        return Result.success(order);
    }
    
    // ==================== 紧急通知 ====================
    
    @PostMapping("/send-notification")
    public Result<Boolean> sendEmergencyNotification(
            @RequestParam Long userId,
            @RequestParam String type,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(required = false) String data) {
        boolean success = familyService.sendEmergencyNotification(userId, type, title, content, data);
        return success ? Result.success(true) : Result.error("发送通知失败");
    }
    
    @GetMapping("/notifications/{familyUserId}")
    public Result<List<FamilyNotification>> getFamilyNotifications(
            @PathVariable Long familyUserId,
            @RequestParam(required = false) Integer isRead) {
        List<FamilyNotification> notifications = familyService.getFamilyNotifications(familyUserId, isRead);
        return Result.success(notifications);
    }
    
    @PostMapping("/notifications/{notificationId}/read")
    public Result<Boolean> markNotificationRead(@PathVariable Long notificationId) {
        boolean success = familyService.markNotificationAsRead(notificationId);
        return success ? Result.success(true) : Result.error("标记已读失败");
    }
    
    @PostMapping("/notifications/{familyUserId}/read-all")
    public Result<Boolean> markAllNotificationsRead(@PathVariable Long familyUserId) {
        boolean success = familyService.markAllNotificationsAsRead(familyUserId);
        return success ? Result.success(true) : Result.error("标记全部已读失败");
    }
    
    @GetMapping("/notifications/{familyUserId}/unread-count")
    public Result<Long> getUnreadNotificationCount(@PathVariable Long familyUserId) {
        Long count = familyService.getUnreadNotificationCount(familyUserId);
        return Result.success(count);
    }
}
