package com.elderly.controller;

import com.elderly.common.Result;
import com.elderly.entity.ActivityRegistration;
import com.elderly.service.ActivityRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/activity-registration")
public class ActivityRegistrationController {
    
    @Autowired
    private ActivityRegistrationService registrationService;
    
    @PostMapping("/register")
    public Result<Boolean> registerActivity(
            @RequestParam Long activityId,
            @RequestParam Long userId,
            @RequestParam String userName,
            @RequestParam String userPhone,
            @RequestParam(required = false) String remark) {
        boolean success = registrationService.registerActivity(activityId, userId, userName, userPhone, remark);
        if (!success) {
            return Result.error("报名失败，可能活动已满或已报名");
        }
        return Result.success(true);
    }
    
    @PostMapping("/cancel")
    public Result<Boolean> cancelRegistration(
            @RequestParam Long activityId,
            @RequestParam Long userId) {
        boolean success = registrationService.cancelRegistration(activityId, userId);
        return success ? Result.success(true) : Result.error("取消报名失败");
    }
    
    @GetMapping("/check")
    public Result<Map<String, Object>> checkRegistration(
            @RequestParam Long activityId,
            @RequestParam Long userId) {
        boolean isRegistered = registrationService.checkUserRegistered(activityId, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("isRegistered", isRegistered);
        return Result.success(result);
    }
    
    @GetMapping("/user/{userId}")
    public Result<List<ActivityRegistration>> getUserRegistrations(@PathVariable Long userId) {
        List<ActivityRegistration> registrations = registrationService.getUserRegistrations(userId);
        return Result.success(registrations);
    }
    
    @GetMapping("/activity/{activityId}")
    public Result<List<ActivityRegistration>> getActivityRegistrations(@PathVariable Long activityId) {
        List<ActivityRegistration> registrations = registrationService.getActivityRegistrations(activityId);
        return Result.success(registrations);
    }
}
