package com.elderly.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.entity.ActivityComment;
import com.elderly.mapper.ActivityCommentMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityCommentService extends ServiceImpl<ActivityCommentMapper, ActivityComment> {
    
    public List<ActivityComment> getCommentsByActivityId(Long activityId) {
        LambdaQueryWrapper<ActivityComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityComment::getActivityId, activityId);
        wrapper.eq(ActivityComment::getDeleted, 0);
        wrapper.orderByDesc(ActivityComment::getCreatedAt);
        return list(wrapper);
    }
    
    public boolean addComment(ActivityComment comment) {
        return save(comment);
    }
    
    public boolean likeComment(Long commentId) {
        ActivityComment comment = getById(commentId);
        if (comment != null) {
            comment.setLikes(comment.getLikes() + 1);
            return updateById(comment);
        }
        return false;
    }
}
