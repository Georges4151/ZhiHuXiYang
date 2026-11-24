package com.elderly.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.entity.Feedback;
import com.elderly.mapper.FeedbackMapper;
import com.elderly.service.FeedbackService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback> implements FeedbackService {
    
    @Override
    public List<Feedback> getByUserId(Long userId) {
        return lambdaQuery()
                .eq(Feedback::getUserId, userId)
                .orderByDesc(Feedback::getCreateTime)
                .list();
    }
    
    @Override
    public boolean addFeedback(Feedback feedback) {
        feedback.setStatus(0);
        return save(feedback);
    }
}
