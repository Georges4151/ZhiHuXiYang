package com.elderly.controller;

import com.elderly.common.Result;
import com.elderly.service.HealthAnalysisService;
import com.elderly.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/health/analysis")
public class HealthAnalysisController {

    @Autowired
    private HealthAnalysisService healthAnalysisService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/analyze")
    public Result<Map<String, Object>> analyzeHealthData(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Result.error("未登录");
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return Result.error("登录已过期，请重新登录");
        }
        Long userId = jwtUtil.getUserIdFromToken(token);

        if (userId == null) {
            return Result.error("无效的登录状态");
        }

        log.info("用户 {} 请求健康分析", userId);
        Map<String, Object> result = healthAnalysisService.analyzeHealthData(userId);

        if (!(boolean) result.get("success")) {
            return Result.error((String) result.get("message"));
        }

        return Result.success("分析完成", result);
    }
}
