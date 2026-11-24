package com.elderly.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.elderly.common.Result;
import com.elderly.entity.HealthData;
import com.elderly.entity.ServiceOrder;
import com.elderly.entity.ElderlyUser;
import com.elderly.mapper.HealthDataMapper;
import com.elderly.mapper.ServiceOrderMapper;
import com.elderly.mapper.ElderlyUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/stats")
public class StatsController {
    
    @Autowired
    private HealthDataMapper healthDataMapper;
    
    @Autowired
    private ServiceOrderMapper serviceOrderMapper;
    
    @Autowired
    private ElderlyUserMapper elderlyUserMapper;
    
    @GetMapping("/health/{userId}")
    public Result<Map<String, Object>> getHealthStats(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "7") Integer days) {
        Map<String, Object> stats = new HashMap<>();
        
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusDays(days);
        
        LambdaQueryWrapper<HealthData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthData::getUserId, userId);
        wrapper.between(HealthData::getMeasureTime, startTime, endTime);
        wrapper.orderByAsc(HealthData::getMeasureTime);
        List<HealthData> healthDataList = healthDataMapper.selectList(wrapper);
        
        List<Map<String, Object>> bloodPressure = new ArrayList<>();
        List<Map<String, Object>> bloodSugar = new ArrayList<>();
        List<Map<String, Object>> heartRate = new ArrayList<>();
        
        for (HealthData data : healthDataList) {
            Map<String, Object> bpItem = new HashMap<>();
            bpItem.put("date", data.getMeasureTime().toString());
            bpItem.put("high", data.getBloodPressureHigh());
            bpItem.put("low", data.getBloodPressureLow());
            bloodPressure.add(bpItem);
            
            Map<String, Object> bsItem = new HashMap<>();
            bsItem.put("date", data.getMeasureTime().toString());
            bsItem.put("value", data.getBloodSugar());
            bloodSugar.add(bsItem);
            
            Map<String, Object> hrItem = new HashMap<>();
            hrItem.put("date", data.getMeasureTime().toString());
            hrItem.put("value", data.getHeartRate());
            heartRate.add(hrItem);
        }
        
        stats.put("bloodPressure", bloodPressure);
        stats.put("bloodSugar", bloodSugar);
        stats.put("heartRate", heartRate);
        
        return Result.success(stats);
    }
    
    @GetMapping("/order/{userId}")
    public Result<Map<String, Object>> getOrderStats(@PathVariable Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        LambdaQueryWrapper<ServiceOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceOrder::getUserId, userId);
        List<ServiceOrder> orders = serviceOrderMapper.selectList(wrapper);
        
        int total = orders.size();
        int pending = 0;
        int inProgress = 0;
        int completed = 0;
        int cancelled = 0;
        
        Map<String, Integer> serviceTypeCount = new HashMap<>();
        
        for (ServiceOrder order : orders) {
            Integer status = order.getOrderStatus();
            if (status != null) {
                if (status == 0) pending++;
                else if (status == 1) inProgress++;
                else if (status == 2) completed++;
                else if (status == 3) cancelled++;
            }
            
            String serviceName = order.getServiceName();
            if (serviceName != null) {
                serviceTypeCount.put(serviceName, serviceTypeCount.getOrDefault(serviceName, 0) + 1);
            }
        }
        
        stats.put("total", total);
        stats.put("pending", pending);
        stats.put("inProgress", inProgress);
        stats.put("completed", completed);
        stats.put("cancelled", cancelled);
        stats.put("serviceTypeCount", serviceTypeCount);
        
        return Result.success(stats);
    }
    
    @GetMapping("/overview")
    public Result<Map<String, Object>> getSystemOverview() {
        Map<String, Object> overview = new HashMap<>();
        
        Long userCount = elderlyUserMapper.selectCount(null);
        overview.put("userCount", userCount);
        
        Long orderCount = serviceOrderMapper.selectCount(null);
        overview.put("orderCount", orderCount);
        
        overview.put("satisfactionRate", 95);
        overview.put("serviceTypes", 10);
        
        return Result.success(overview);
    }
}
