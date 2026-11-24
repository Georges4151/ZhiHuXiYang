package com.elderly.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.common.Result;
import com.elderly.entity.EmergencyCall;
import com.elderly.mapper.EmergencyCallMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/emergency")
public class AdminEmergencyController {

    @Autowired
    private EmergencyCallMapper emergencyCallMapper;

    @GetMapping("/list")
    public Result<Map<String, Object>> getEmergencyList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) Integer callStatus) {
        
        Page<EmergencyCall> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<EmergencyCall> wrapper = new LambdaQueryWrapper<>();
        
        if (userName != null && !userName.isEmpty()) {
            wrapper.like(EmergencyCall::getUserName, userName);
        }
        if (callStatus != null) {
            wrapper.eq(EmergencyCall::getCallStatus, callStatus);
        }
        wrapper.eq(EmergencyCall::getDeleted, 0);
        wrapper.orderByDesc(EmergencyCall::getCreateTime);
        
        Page<EmergencyCall> result = emergencyCallMapper.selectPage(pageParam, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("list", result.getRecords());
        data.put("total", result.getTotal());
        data.put("page", page);
        data.put("pageSize", pageSize);
        
        return Result.success(data);
    }

    @GetMapping("/{id}")
    public Result<EmergencyCall> getEmergencyDetail(@PathVariable Long id) {
        EmergencyCall call = emergencyCallMapper.selectById(id);
        if (call == null) {
            return Result.error("记录不存在");
        }
        return Result.success(call);
    }

    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        Integer callStatus = params.get("callStatus");
        EmergencyCall call = new EmergencyCall();
        call.setId(id);
        call.setCallStatus(callStatus);
        int rows = emergencyCallMapper.updateById(call);
        return rows > 0 ? Result.success(true) : Result.error("更新失败");
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("pending", 3);
        stats.put("processing", 5);
        stats.put("completed", 128);
        return Result.success(stats);
    }
}
