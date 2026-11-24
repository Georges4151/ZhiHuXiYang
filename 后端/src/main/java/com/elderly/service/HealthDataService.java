package com.elderly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.entity.HealthData;

import java.util.List;

public interface HealthDataService extends IService<HealthData> {
    
    List<HealthData> getRecentByUserId(Long userId, Integer limit);
    
    HealthData getLatestByUserId(Long userId);
}
