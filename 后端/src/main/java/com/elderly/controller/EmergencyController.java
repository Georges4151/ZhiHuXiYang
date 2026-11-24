package com.elderly.controller;

import com.elderly.common.Result;
import com.elderly.entity.EmergencyCall;
import com.elderly.service.EmergencyCallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emergency")
public class EmergencyController {
    
    @Autowired
    private EmergencyCallService emergencyCallService;
    
    @PostMapping("/call")
    public Result<EmergencyCall> createCall(@RequestBody EmergencyCall call) {
        EmergencyCall created = emergencyCallService.createEmergencyCall(call);
        return Result.success(created);
    }
    
    @PutMapping("/status")
    public Result<Boolean> updateStatus(
            @RequestParam Long callId,
            @RequestParam Integer status,
            @RequestParam(required = false) Long responderId) {
        boolean success = emergencyCallService.updateCallStatus(callId, status, responderId);
        return success ? Result.success() : Result.error("更新失败");
    }
    
    @GetMapping("/detail/{id}")
    public Result<EmergencyCall> getDetail(@PathVariable Long id) {
        EmergencyCall call = emergencyCallService.getById(id);
        return Result.success(call);
    }
}
