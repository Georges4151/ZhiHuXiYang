package com.elderly.controller;

import com.elderly.common.Result;
import com.elderly.dto.HealthDataRequest;
import com.elderly.entity.HealthData;
import com.elderly.service.HealthDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/health")
public class HealthController {
    
    @Autowired
    private HealthDataService healthDataService;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @PostMapping("/add")
    public Result<Boolean> addHealthData(@RequestBody HealthDataRequest request) {
        HealthData healthData = new HealthData();
        healthData.setUserId(request.getUserId());
        healthData.setBloodPressureHigh(request.getBloodPressureHigh());
        healthData.setBloodPressureLow(request.getBloodPressureLow());
        healthData.setHeartRate(request.getHeartRate());
        healthData.setBloodSugar(request.getBloodSugar());
        healthData.setTemperature(request.getTemperature());
        healthData.setWeight(request.getWeight());
        healthData.setRemark(request.getRemark());
        
        if (request.getMeasureTime() != null && !request.getMeasureTime().isEmpty()) {
            try {
                healthData.setMeasureTime(LocalDateTime.parse(request.getMeasureTime(), FORMATTER));
            } catch (Exception e) {
                healthData.setMeasureTime(LocalDateTime.now());
            }
        } else {
            healthData.setMeasureTime(LocalDateTime.now());
        }
        
        boolean success = healthDataService.save(healthData);
        return success ? Result.success() : Result.error("添加失败");
    }
    
    @GetMapping("/latest/{userId}")
    public Result<HealthData> getLatestHealth(@PathVariable Long userId) {
        HealthData data = healthDataService.getLatestByUserId(userId);
        return Result.success(data);
    }
    
    @GetMapping("/recent/{userId}")
    public Result<List<HealthData>> getRecentHealth(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "7") Integer limit) {
        List<HealthData> list = healthDataService.getRecentByUserId(userId, limit);
        return Result.success(list);
    }
}
