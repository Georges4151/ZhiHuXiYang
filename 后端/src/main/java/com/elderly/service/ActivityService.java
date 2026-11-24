package com.elderly.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.entity.Activity;
import com.elderly.mapper.ActivityMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityService extends ServiceImpl<ActivityMapper, Activity> {
    
    public List<Activity> getActivitiesByCategory(Integer category) {
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        if (category != null && category > 0) {
            wrapper.eq(Activity::getCategory, category);
        }
        wrapper.eq(Activity::getDeleted, 0);
        wrapper.orderByDesc(Activity::getCreatedAt);
        return list(wrapper);
    }
    
    public Activity getActivityById(Long id) {
        return getById(id);
    }
    
    public boolean joinActivity(Long activityId) {
        Activity activity = getById(activityId);
        if (activity != null && activity.getCurrentParticipants() < activity.getMaxParticipants()) {
            activity.setCurrentParticipants(activity.getCurrentParticipants() + 1);
            return updateById(activity);
        }
        return false;
    }
    
    public boolean cancelJoin(Long activityId) {
        Activity activity = getById(activityId);
        if (activity != null && activity.getCurrentParticipants() > 0) {
            activity.setCurrentParticipants(activity.getCurrentParticipants() - 1);
            return updateById(activity);
        }
        return false;
    }
    
    public boolean likeActivity(Long activityId) {
        Activity activity = getById(activityId);
        if (activity != null) {
            activity.setLikes(activity.getLikes() + 1);
            return updateById(activity);
        }
        return false;
    }
}
