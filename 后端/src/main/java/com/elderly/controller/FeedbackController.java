package com.elderly.controller;

import com.elderly.common.Result;
import com.elderly.entity.Feedback;
import com.elderly.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {
    
    @Autowired
    private FeedbackService feedbackService;
    
    @GetMapping("/list/{userId}")
    public Result<List<Feedback>> getList(@PathVariable Long userId) {
        List<Feedback> list = feedbackService.getByUserId(userId);
        return Result.success(list);
    }
    
    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody Feedback feedback) {
        boolean success = feedbackService.addFeedback(feedback);
        return Result.success(success);
    }
}
