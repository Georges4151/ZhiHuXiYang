package com.elderly.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.entity.Activity;
import com.elderly.entity.ActivityRegistration;
import com.elderly.mapper.ActivityMapper;
import com.elderly.mapper.ActivityRegistrationMapper;
import com.elderly.service.ActivityRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ActivityRegistrationServiceImpl extends ServiceImpl<ActivityRegistrationMapper, ActivityRegistration> implements ActivityRegistrationService {
    
    @Autowired
    private ActivityMapper activityMapper;
    
    @Override
    @Transactional
    public boolean registerActivity(Long activityId, Long userId, String userName, String userPhone, String remark) {
        // 检查是否已经报名
        if (checkUserRegistered(activityId, userId)) {
            return true;
        }
        
        // 检查活动是否已满
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null || activity.getDeleted() == 1) {
            return false;
        }
        
        if (activity.getMaxParticipants() != null && 
            activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
            return false;
        }
        
        ActivityRegistration registration = new ActivityRegistration();
        registration.setActivityId(activityId);
        registration.setUserId(userId);
        registration.setUserName(userName);
        registration.setUserPhone(userPhone);
        registration.setRemark(remark);
        registration.setStatus(0); // 待确认
        
        boolean success = save(registration);
        
        if (success) {
            // 更新活动报名人数
            activity.setCurrentParticipants(activity.getCurrentParticipants() + 1);
            activityMapper.updateById(activity);
        }
        
        return success;
    }
    
    @Override
    @Transactional
    public boolean cancelRegistration(Long activityId, Long userId) {
        ActivityRegistration registration = lambdaQuery()
                .eq(ActivityRegistration::getActivityId, activityId)
                .eq(ActivityRegistration::getUserId, userId)
                .one();
        
        if (registration == null) {
            return false;
        }
        
        boolean success = removeById(registration.getId());
        
        if (success) {
            // 更新活动报名人数
            Activity activity = activityMapper.selectById(activityId);
            if (activity != null && activity.getCurrentParticipants() > 0) {
                activity.setCurrentParticipants(activity.getCurrentParticipants() - 1);
                activityMapper.updateById(activity);
            }
        }
        
        return success;
    }
    
    @Override
    public boolean checkUserRegistered(Long activityId, Long userId) {
        return baseMapper.checkUserRegistered(activityId, userId) > 0;
    }
    
    @Override
    public List<ActivityRegistration> getUserRegistrations(Long userId) {
        return lambdaQuery()
                .eq(ActivityRegistration::getUserId, userId)
                .orderByDesc(ActivityRegistration::getRegisteredAt)
                .list();
    }
    
    @Override
    public List<ActivityRegistration> getActivityRegistrations(Long activityId) {
        return lambdaQuery()
                .eq(ActivityRegistration::getActivityId, activityId)
                .orderByDesc(ActivityRegistration::getRegisteredAt)
                .list();
    }
}
