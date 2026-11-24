package com.elderly.service;

import com.elderly.entity.FamilyNotification;
import com.elderly.entity.ServiceOrder;

import java.util.List;
import java.util.Map;

public interface FamilyService {
    
    // 家属绑定相关
    boolean bindFamily(Long userId, Long familyUserId, String name, String phone, String relation);
    
    boolean addFamilyManual(Long userId, String name, String phone, String relation);
    
    boolean unbindFamily(Long relationId);
    
    List<Map<String, Object>> getFamilyMembers(Long userId);
    
    List<Map<String, Object>> getBindUsers(Long familyUserId);
    
    // 健康数据查看
    List<Map<String, Object>> getFamilyHealthData(Long userId, Long familyUserId, String dataType, Integer days);
    
    Map<String, Object> getFamilyHealthStats(Long userId, Long familyUserId);
    
    // 服务进度跟踪
    List<Map<String, Object>> getFamilyServiceOrders(Long userId, Long familyUserId, Integer status);
    
    ServiceOrder getFamilyServiceOrderDetail(Long orderId, Long familyUserId);
    
    // 紧急通知
    boolean sendEmergencyNotification(Long userId, String type, String title, String content, String data);
    
    List<FamilyNotification> getFamilyNotifications(Long familyUserId, Integer isRead);
    
    boolean markNotificationAsRead(Long notificationId);
    
    boolean markAllNotificationsAsRead(Long familyUserId);
    
    Long getUnreadNotificationCount(Long familyUserId);
}
