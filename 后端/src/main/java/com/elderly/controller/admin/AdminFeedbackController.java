package com.elderly.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.common.Result;
import com.elderly.entity.Feedback;
import com.elderly.mapper.FeedbackMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/feedback")
public class AdminFeedbackController {

    @Autowired
    private FeedbackMapper feedbackMapper;

    @GetMapping("/list")
    public Result<Map<String, Object>> getFeedbackList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        
        Page<Feedback> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Feedback> wrapper = new LambdaQueryWrapper<>();
        
        if (status != null) {
            wrapper.eq(Feedback::getStatus, status);
        }
        wrapper.eq(Feedback::getDeleted, 0);
        wrapper.orderByDesc(Feedback::getCreateTime);
        
        Page<Feedback> result = feedbackMapper.selectPage(pageParam, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("list", result.getRecords());
        data.put("total", result.getTotal());
        data.put("page", page);
        data.put("pageSize", pageSize);
        
        return Result.success(data);
    }

    @GetMapping("/{id}")
    public Result<Feedback> getFeedbackDetail(@PathVariable Long id) {
        Feedback feedback = feedbackMapper.selectById(id);
        if (feedback == null) {
            return Result.error("反馈不存在");
        }
        return Result.success(feedback);
    }

    @PutMapping("/{id}/reply")
    public Result<Boolean> replyFeedback(@PathVariable Long id, @RequestBody Map<String, String> params) {
        String reply = params.get("reply");
        Feedback feedback = new Feedback();
        feedback.setId(id);
        feedback.setReply(reply);
        feedback.setStatus(1);
        int rows = feedbackMapper.updateById(feedback);
        return rows > 0 ? Result.success(true) : Result.error("回复失败");
    }
}
