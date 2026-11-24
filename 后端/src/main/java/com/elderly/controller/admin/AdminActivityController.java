package com.elderly.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.common.Result;
import com.elderly.entity.Activity;
import com.elderly.entity.ActivityComment;
import com.elderly.mapper.ActivityMapper;
import com.elderly.mapper.ActivityCommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/activity")
public class AdminActivityController {

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private ActivityCommentMapper commentMapper;

    @GetMapping("/list")
    public Result<Map<String, Object>> getActivityList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer category,
            @RequestParam(required = false) String title) {
        
        Page<Activity> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        
        if (category != null) {
            wrapper.eq(Activity::getCategory, category);
        }
        if (title != null && !title.isEmpty()) {
            wrapper.like(Activity::getTitle, title);
        }
        wrapper.eq(Activity::getDeleted, 0);
        wrapper.orderByDesc(Activity::getCreatedAt);
        
        Page<Activity> result = activityMapper.selectPage(pageParam, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("list", result.getRecords());
        data.put("total", result.getTotal());
        data.put("page", page);
        data.put("pageSize", pageSize);
        
        return Result.success(data);
    }

    @GetMapping("/{id}")
    public Result<Activity> getActivityDetail(@PathVariable Long id) {
        Activity activity = activityMapper.selectById(id);
        if (activity == null) {
            return Result.error("活动不存在");
        }
        return Result.success(activity);
    }

    @PostMapping
    public Result<Boolean> addActivity(@RequestBody Activity activity) {
        activity.setCurrentParticipants(0);
        activity.setLikes(0);
        int rows = activityMapper.insert(activity);
        return rows > 0 ? Result.success(true) : Result.error("添加失败");
    }

    @PutMapping("/{id}")
    public Result<Boolean> updateActivity(@PathVariable Long id, @RequestBody Activity activity) {
        activity.setId(id);
        int rows = activityMapper.updateById(activity);
        return rows > 0 ? Result.success(true) : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteActivity(@PathVariable Long id) {
        LambdaUpdateWrapper<Activity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Activity::getId, id);
        wrapper.set(Activity::getDeleted, 1);
        int rows = activityMapper.update(null, wrapper);
        return rows > 0 ? Result.success(true) : Result.error("删除失败");
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> getActivityStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", 15);
        stats.put("ongoing", 8);
        stats.put("participants", 156);
        return Result.success(stats);
    }
}
