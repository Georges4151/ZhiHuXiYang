package com.elderly.controller;

import com.elderly.common.Result;
import com.elderly.entity.Activity;
import com.elderly.entity.ActivityComment;
import com.elderly.service.ActivityService;
import com.elderly.service.ActivityCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {
    
    @Autowired
    private ActivityService activityService;
    
    @Autowired
    private ActivityCommentService commentService;
    
    @GetMapping("/list")
    public Result<List<Activity>> getActivityList(
            @RequestParam(required = false) Integer category) {
        List<Activity> activities = activityService.getActivitiesByCategory(category);
        return Result.success(activities);
    }
    
    @GetMapping("/{id}")
    public Result<Activity> getActivityDetail(@PathVariable Long id) {
        Activity activity = activityService.getActivityById(id);
        if (activity == null) {
            return Result.error("活动不存在");
        }
        return Result.success(activity);
    }
    
    @PostMapping("/join/{id}")
    public Result<Boolean> joinActivity(@PathVariable Long id) {
        boolean success = activityService.joinActivity(id);
        if (success) {
            return Result.success(true);
        }
        return Result.error("报名失败，名额已满");
    }
    
    @PostMapping("/cancel/{id}")
    public Result<Boolean> cancelJoin(@PathVariable Long id) {
        boolean success = activityService.cancelJoin(id);
        if (success) {
            return Result.success(true);
        }
        return Result.error("取消报名失败");
    }
    
    @PostMapping("/like/{id}")
    public Result<Boolean> likeActivity(@PathVariable Long id) {
        boolean success = activityService.likeActivity(id);
        return success ? Result.success(true) : Result.error("点赞失败");
    }
    
    @GetMapping("/comments/{activityId}")
    public Result<List<ActivityComment>> getComments(@PathVariable Long activityId) {
        List<ActivityComment> comments = commentService.getCommentsByActivityId(activityId);
        return Result.success(comments);
    }
    
    @PostMapping("/comment")
    public Result<Boolean> addComment(@RequestBody ActivityComment comment) {
        comment.setLikes(0);
        comment.setCreatedAt(LocalDateTime.now());
        boolean success = commentService.addComment(comment);
        return success ? Result.success(true) : Result.error("评论失败");
    }
    
    @PostMapping("/comment/like/{id}")
    public Result<Boolean> likeComment(@PathVariable Long id) {
        boolean success = commentService.likeComment(id);
        return success ? Result.success(true) : Result.error("点赞失败");
    }
}
