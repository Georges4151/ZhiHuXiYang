package com.elderly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.entity.HealthData;
import com.elderly.mapper.HealthDataMapper;
import com.elderly.service.HealthDataService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HealthDataServiceImpl extends ServiceImpl<HealthDataMapper, HealthData> implements HealthDataService {
    
    @Override
    public List<HealthData> getRecentByUserId(Long userId, Integer limit) {
        return this.list(new LambdaQueryWrapper<HealthData>()
                .eq(HealthData::getUserId, userId)
                .orderByDesc(HealthData::getMeasureTime)
                .last("LIMIT " + limit));
    }
    
    @Override
    public HealthData getLatestByUserId(Long userId) {
        return this.getOne(new LambdaQueryWrapper<HealthData>()
                .eq(HealthData::getUserId, userId)
                .orderByDesc(HealthData::getMeasureTime)
                .last("LIMIT 1"));
    }
}
