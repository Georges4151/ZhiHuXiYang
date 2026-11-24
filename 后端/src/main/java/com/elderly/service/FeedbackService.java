package com.elderly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.entity.Feedback;

import java.util.List;

public interface FeedbackService extends IService<Feedback> {
    
    List<Feedback> getByUserId(Long userId);
    
    boolean addFeedback(Feedback feedback);
}
