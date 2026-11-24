package com.elderly.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.entity.FamilyActivity;
import com.elderly.mapper.FamilyActivityMapper;
import com.elderly.service.FamilyActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FamilyActivityServiceImpl extends ServiceImpl<FamilyActivityMapper, FamilyActivity> implements FamilyActivityService {
    
    @Autowired
    private FamilyActivityMapper familyActivityMapper;
    
    @Override
    public List<FamilyActivity> getRecentActivities(Long userId, int limit) {
        return familyActivityMapper.selectRecentByUserId(userId, limit);
    }
    
    @Override
    public void recordLocationShare(Long userId, String address, double latitude, double longitude) {
        FamilyActivity activity = new FamilyActivity();
        activity.setUserId(userId);
        activity.setType(FamilyActivity.TYPE_LOCATION);
        activity.setTitle("位置共享");
        activity.setDescription("分享了当前位置：" + address);
        activity.setExtraData("{\"latitude\":" + latitude + ",\"longitude\":" + longitude + ",\"address\":\"" + address + "\"}");
        save(activity);
    }
    
    @Override
    public void recordHealthShare(Long userId, String summary) {
        FamilyActivity activity = new FamilyActivity();
        activity.setUserId(userId);
        activity.setType(FamilyActivity.TYPE_HEALTH);
        activity.setTitle("健康报告");
        activity.setDescription("分享了健康数据：" + summary);
        save(activity);
    }
    
    @Override
    public void recordAlert(Long userId, String description) {
        FamilyActivity activity = new FamilyActivity();
        activity.setUserId(userId);
        activity.setType(FamilyActivity.TYPE_ALERT);
        activity.setTitle("紧急呼叫");
        activity.setDescription(description);
        save(activity);
    }
}
