package com.elderly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.entity.ActivityRegistration;

import java.util.List;

public interface ActivityRegistrationService extends IService<ActivityRegistration> {
    
    boolean registerActivity(Long activityId, Long userId, String userName, String userPhone, String remark);
    
    boolean cancelRegistration(Long activityId, Long userId);
    
    boolean checkUserRegistered(Long activityId, Long userId);
    
    List<ActivityRegistration> getUserRegistrations(Long userId);
    
    List<ActivityRegistration> getActivityRegistrations(Long activityId);
}
