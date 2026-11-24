package com.elderly.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.common.Result;
import com.elderly.entity.HealthData;
import com.elderly.mapper.HealthDataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/health")
public class AdminHealthController {

    @Autowired
    private HealthDataMapper healthDataMapper;

    @GetMapping("/list")
    public Result<Map<String, Object>> getHealthList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long userId) {
        
        Page<HealthData> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<HealthData> wrapper = new LambdaQueryWrapper<>();
        
        if (userId != null) {
            wrapper.eq(HealthData::getUserId, userId);
        }
        wrapper.eq(HealthData::getDeleted, 0);
        wrapper.orderByDesc(HealthData::getMeasureTime);
        
        Page<HealthData> result = healthDataMapper.selectPage(pageParam, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("list", result.getRecords());
        data.put("total", result.getTotal());
        data.put("page", page);
        data.put("pageSize", pageSize);
        
        return Result.success(data);
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRecords", 1256);
        stats.put("monitoringUsers", 89);
        stats.put("abnormalCount", 12);
        stats.put("onTimeRate", 98);
        return Result.success(stats);
    }
}
