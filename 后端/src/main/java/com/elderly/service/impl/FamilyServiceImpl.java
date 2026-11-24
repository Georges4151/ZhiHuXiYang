package com.elderly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.elderly.entity.*;
import com.elderly.mapper.*;
import com.elderly.service.FamilyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class FamilyServiceImpl implements FamilyService {
    
    @Autowired
    private FamilyRelationMapper familyRelationMapper;
    
    @Autowired
    private ElderlyUserMapper userMapper;
    
    @Autowired
    private HealthDataMapper healthDataMapper;
    
    @Autowired
    private ServiceOrderMapper orderMapper;
    
    @Autowired
    private FamilyNotificationMapper notificationMapper;
    
    @Override
    @Transactional
    public boolean bindFamily(Long userId, Long familyUserId, String name, String phone, String relation) {
        // 检查是否已绑定
        Long count = familyRelationMapper.selectCount(
            new LambdaQueryWrapper<FamilyRelation>()
                .eq(FamilyRelation::getUserId, userId)
                .eq(FamilyRelation::getFamilyUserId, familyUserId)
        );
        
        if (count > 0) {
            return true;
        }
        
        FamilyRelation familyRelation = new FamilyRelation();
        familyRelation.setUserId(userId);
        familyRelation.setFamilyUserId(familyUserId);
        familyRelation.setName(name);
        familyRelation.setPhone(phone);
        familyRelation.setRelation(relation);
        familyRelation.setIsPrimary(0);
        
        return familyRelationMapper.insert(familyRelation) > 0;
    }
    
    @Override
    @Transactional
    public boolean addFamilyManual(Long userId, String name, String phone, String relation) {
        // 手动添加家属（非平台用户），familyUserId 设为 null
        FamilyRelation familyRelation = new FamilyRelation();
        familyRelation.setUserId(userId);
        familyRelation.setFamilyUserId(null);
        familyRelation.setName(name);
        familyRelation.setPhone(phone);
        familyRelation.setRelation(relation);
        familyRelation.setIsPrimary(0);
        
        return familyRelationMapper.insert(familyRelation) > 0;
    }
    
    @Override
    @Transactional
    public boolean unbindFamily(Long relationId) {
        return familyRelationMapper.deleteById(relationId) > 0;
    }
    
    @Override
    public List<Map<String, Object>> getFamilyMembers(Long userId) {
        List<FamilyRelation> relations = familyRelationMapper.selectList(
            new LambdaQueryWrapper<FamilyRelation>()
                .eq(FamilyRelation::getUserId, userId)
        );
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (FamilyRelation relation : relations) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", relation.getId());
            map.put("name", relation.getName());
            map.put("phone", relation.getPhone());
            map.put("relation", relation.getRelation());
            map.put("isPrimary", relation.getIsPrimary());
            
            // 获取家属用户信息
            ElderlyUser familyUser = userMapper.selectById(relation.getFamilyUserId());
            if (familyUser != null) {
                map.put("familyUserId", familyUser.getId());
                map.put("familyUserName", familyUser.getName());
                map.put("familyUserAvatar", familyUser.getAvatar());
            }
            
            result.add(map);
        }
        
        return result;
    }
    
    @Override
    public List<Map<String, Object>> getBindUsers(Long familyUserId) {
        List<FamilyRelation> relations = familyRelationMapper.selectList(
            new LambdaQueryWrapper<FamilyRelation>()
                .eq(FamilyRelation::getFamilyUserId, familyUserId)
        );
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (FamilyRelation relation : relations) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", relation.getId());
            map.put("relation", relation.getRelation());
            
            // 获取老人用户信息
            ElderlyUser user = userMapper.selectById(relation.getUserId());
            if (user != null) {
                map.put("userId", user.getId());
                map.put("userName", user.getName());
                map.put("userAvatar", user.getAvatar());
                map.put("userPhone", user.getPhone());
                map.put("healthCondition", user.getHealthCondition());
            }
            
            result.add(map);
        }
        
        return result;
    }
    
    @Override
    public List<Map<String, Object>> getFamilyHealthData(Long userId, Long familyUserId, String dataType, Integer days) {
        // 验证家属关系
        Long count = familyRelationMapper.selectCount(
            new LambdaQueryWrapper<FamilyRelation>()
                .eq(FamilyRelation::getUserId, userId)
                .eq(FamilyRelation::getFamilyUserId, familyUserId)
        );
        
        if (count == 0) {
            return new ArrayList<>();
        }
        
        LambdaQueryWrapper<HealthData> wrapper = new LambdaQueryWrapper<HealthData>()
            .eq(HealthData::getUserId, userId);
        
        if (days != null) {
            LocalDateTime startTime = LocalDateTime.now().minusDays(days);
            wrapper.ge(HealthData::getMeasureTime, startTime);
        }
        
        wrapper.orderByDesc(HealthData::getMeasureTime);
        
        List<HealthData> healthDataList = healthDataMapper.selectList(wrapper);
        
        // 转换为前端需要的格式
        List<Map<String, Object>> result = new ArrayList<>();
        for (HealthData data : healthDataList) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", data.getId());
            map.put("recordTime", data.getMeasureTime());
            map.put("remark", data.getRemark());
            
            // 根据数据类型设置dataType和dataValue
            if (data.getBloodPressureHigh() != null && data.getBloodPressureLow() != null) {
                map.put("dataType", "血压");
                map.put("dataValue", data.getBloodPressureHigh() + "/" + data.getBloodPressureLow() + " mmHg");
                // 判断血压是否异常（高压>140或低压>90为异常）
                boolean isAbnormal = data.getBloodPressureHigh().compareTo(new BigDecimal("140")) > 0 
                    || data.getBloodPressureLow().compareTo(new BigDecimal("90")) > 0;
                map.put("isAbnormal", isAbnormal ? 1 : 0);
            } else if (data.getHeartRate() != null) {
                map.put("dataType", "心率");
                map.put("dataValue", data.getHeartRate() + " 次/分");
                boolean isAbnormal = data.getHeartRate().compareTo(new BigDecimal("100")) > 0 
                    || data.getHeartRate().compareTo(new BigDecimal("60")) < 0;
                map.put("isAbnormal", isAbnormal ? 1 : 0);
            } else if (data.getBloodSugar() != null) {
                map.put("dataType", "血糖");
                map.put("dataValue", data.getBloodSugar() + " mmol/L");
                boolean isAbnormal = data.getBloodSugar().compareTo(new BigDecimal("7.0")) > 0;
                map.put("isAbnormal", isAbnormal ? 1 : 0);
            } else if (data.getTemperature() != null) {
                map.put("dataType", "体温");
                map.put("dataValue", data.getTemperature() + " °C");
                boolean isAbnormal = data.getTemperature().compareTo(new BigDecimal("37.3")) > 0;
                map.put("isAbnormal", isAbnormal ? 1 : 0);
            } else if (data.getWeight() != null) {
                map.put("dataType", "体重");
                map.put("dataValue", data.getWeight() + " kg");
                map.put("isAbnormal", 0);
            }
            
            result.add(map);
        }
        
        return result;
    }
    
    @Override
    public Map<String, Object> getFamilyHealthStats(Long userId, Long familyUserId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取最近7天的健康数据
        List<Map<String, Object>> recentData = getFamilyHealthData(userId, familyUserId, null, 7);
        
        // 统计各类数据
        Map<String, List<Map<String, Object>>> groupedData = new HashMap<>();
        for (Map<String, Object> data : recentData) {
            String type = (String) data.get("dataType");
            groupedData.computeIfAbsent(type, k -> new ArrayList<>()).add(data);
        }
        
        stats.put("recentData", recentData);
        stats.put("groupedData", groupedData);
        stats.put("totalRecords", recentData.size());
        
        // 检查是否有异常数据
        boolean hasAbnormal = recentData.stream()
            .anyMatch(d -> d.get("isAbnormal") != null && (Integer) d.get("isAbnormal") == 1);
        stats.put("hasAbnormal", hasAbnormal);
        
        return stats;
    }
    
    @Override
    public List<Map<String, Object>> getFamilyServiceOrders(Long userId, Long familyUserId, Integer status) {
        // 验证家属关系
        Long count = familyRelationMapper.selectCount(
            new LambdaQueryWrapper<FamilyRelation>()
                .eq(FamilyRelation::getUserId, userId)
                .eq(FamilyRelation::getFamilyUserId, familyUserId)
        );
        
        if (count == 0) {
            return new ArrayList<>();
        }
        
        LambdaQueryWrapper<ServiceOrder> wrapper = new LambdaQueryWrapper<ServiceOrder>()
            .eq(ServiceOrder::getUserId, userId);
        
        if (status != null) {
            wrapper.eq(ServiceOrder::getOrderStatus, status);
        }
        
        wrapper.orderByDesc(ServiceOrder::getCreateTime);
        
        List<ServiceOrder> orders = orderMapper.selectList(wrapper);
        
        // 转换为前端需要的格式
        List<Map<String, Object>> result = new ArrayList<>();
        for (ServiceOrder order : orders) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", order.getId());
            map.put("orderNo", order.getOrderNo());
            map.put("serviceName", order.getServiceName());
            map.put("amount", order.getAmount());
            map.put("orderStatus", order.getOrderStatus());
            map.put("address", order.getServiceAddress());
            map.put("appointmentTime", order.getAppointmentTime());
            map.put("createdAt", order.getCreateTime());
            map.put("workerName", order.getWorkerName());
            result.add(map);
        }
        
        return result;
    }
    
    @Override
    public ServiceOrder getFamilyServiceOrderDetail(Long orderId, Long familyUserId) {
        ServiceOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            return null;
        }
        
        // 验证家属关系
        Long count = familyRelationMapper.selectCount(
            new LambdaQueryWrapper<FamilyRelation>()
                .eq(FamilyRelation::getUserId, order.getUserId())
                .eq(FamilyRelation::getFamilyUserId, familyUserId)
        );
        
        if (count == 0) {
            return null;
        }
        
        return order;
    }
    
    @Override
    @Transactional
    public boolean sendEmergencyNotification(Long userId, String type, String title, String content, String data) {
        // 获取所有绑定的家属
        List<FamilyRelation> relations = familyRelationMapper.selectList(
            new LambdaQueryWrapper<FamilyRelation>()
                .eq(FamilyRelation::getUserId, userId)
        );
        
        for (FamilyRelation relation : relations) {
            FamilyNotification notification = new FamilyNotification();
            notification.setUserId(userId);
            notification.setFamilyUserId(relation.getFamilyUserId());
            notification.setType(type);
            notification.setTitle(title);
            notification.setContent(content);
            notification.setData(data);
            notification.setIsRead(0);
            
            notificationMapper.insert(notification);
        }
        
        return true;
    }
    
    @Override
    public List<FamilyNotification> getFamilyNotifications(Long familyUserId, Integer isRead) {
        LambdaQueryWrapper<FamilyNotification> wrapper = new LambdaQueryWrapper<FamilyNotification>()
            .eq(FamilyNotification::getFamilyUserId, familyUserId);
        
        if (isRead != null) {
            wrapper.eq(FamilyNotification::getIsRead, isRead);
        }
        
        wrapper.orderByDesc(FamilyNotification::getCreatedAt);
        
        return notificationMapper.selectList(wrapper);
    }
    
    @Override
    @Transactional
    public boolean markNotificationAsRead(Long notificationId) {
        FamilyNotification notification = notificationMapper.selectById(notificationId);
        if (notification == null) {
            return false;
        }
        
        notification.setIsRead(1);
        return notificationMapper.updateById(notification) > 0;
    }
    
    @Override
    @Transactional
    public boolean markAllNotificationsAsRead(Long familyUserId) {
        FamilyNotification notification = new FamilyNotification();
        notification.setIsRead(1);
        
        LambdaQueryWrapper<FamilyNotification> wrapper = new LambdaQueryWrapper<FamilyNotification>()
            .eq(FamilyNotification::getFamilyUserId, familyUserId)
            .eq(FamilyNotification::getIsRead, 0);
        
        return notificationMapper.update(notification, wrapper) > 0;
    }
    
    @Override
    public Long getUnreadNotificationCount(Long familyUserId) {
        return notificationMapper.selectCount(
            new LambdaQueryWrapper<FamilyNotification>()
                .eq(FamilyNotification::getFamilyUserId, familyUserId)
                .eq(FamilyNotification::getIsRead, 0)
        );
    }
}
