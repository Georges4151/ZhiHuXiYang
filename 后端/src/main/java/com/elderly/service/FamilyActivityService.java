package com.elderly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.entity.FamilyActivity;

import java.util.List;

public interface FamilyActivityService extends IService<FamilyActivity> {
    
    List<FamilyActivity> getRecentActivities(Long userId, int limit);
    
    void recordLocationShare(Long userId, String address, double latitude, double longitude);
    
    void recordHealthShare(Long userId, String summary);
    
    void recordAlert(Long userId, String description);
}
