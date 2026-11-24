package com.elderly.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.common.Result;
import com.elderly.entity.ConsultationRecord;
import com.elderly.service.ConsultationRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/consultation")
public class ConsultationController {
    
    @Autowired
    private ConsultationRecordService consultationService;
    
    @PostMapping("/chat")
    public Result<Map<String, Object>> chat(@RequestBody Map<String, Object> params) {
        Long userId = Long.parseLong(params.get("userId").toString());
        String question = params.get("question").toString();
        
        String answer = consultationService.chatWithDeepSeek(userId, question);
        
        Map<String, Object> result = new HashMap<>();
        result.put("answer", answer);
        return Result.success(result);
    }
    
    @GetMapping("/history/{userId}")
    public Result<List<ConsultationRecord>> getHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "20") Integer limit) {
        List<ConsultationRecord> history = consultationService.getHistoryByUserId(userId, limit);
        return Result.success(history);
    }
    
    @GetMapping("/page/{userId}")
    public Result<Page<ConsultationRecord>> getPage(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<ConsultationRecord> page = consultationService.getPageByUserId(userId, pageNum, pageSize);
        return Result.success(page);
    }
    
    @PostMapping("/feedback")
    public Result<Boolean> feedback(@RequestBody ConsultationRecord record) {
        boolean success = consultationService.updateById(record);
        return success ? Result.success() : Result.error("反馈失败");
    }
}
