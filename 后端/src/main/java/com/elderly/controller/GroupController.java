package com.elderly.controller;

import com.elderly.common.Result;
import com.elderly.dto.JoinGroupRequest;
import com.elderly.dto.LikeRequest;
import com.elderly.entity.GroupMember;
import com.elderly.entity.InterestGroup;
import com.elderly.service.InterestGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groups")
public class GroupController {
    
    @Autowired
    private InterestGroupService groupService;
    
    @GetMapping("/list")
    public Result<List<InterestGroup>> getGroupList(
            @RequestParam(required = false) Integer category,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        List<InterestGroup> groups = groupService.getGroupList(category, page, size);
        return Result.success(groups);
    }
    
    @GetMapping("/{groupId}")
    public Result<InterestGroup> getGroupDetail(@PathVariable Long groupId) {
        InterestGroup group = groupService.getGroupDetail(groupId);
        if (group == null) {
            return Result.error("群组不存在");
        }
        return Result.success(group);
    }
    
    @PostMapping("/create")
    public Result<Boolean> createGroup(
            @RequestParam Long userId,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam(required = false) String coverImage,
            @RequestParam(required = false, defaultValue = "0") Integer category) {
        boolean success = groupService.createGroup(userId, name, description, coverImage, category);
        return success ? Result.success(true) : Result.error("创建失败");
    }
    
    @PostMapping("/{groupId}/join")
    public Result<Boolean> joinGroup(
            @PathVariable Long groupId,
            @RequestBody JoinGroupRequest request) {
        boolean success = groupService.joinGroup(
            groupId, 
            request.getUserId(), 
            request.getUserName(), 
            request.getUserAvatar()
        );
        return success ? Result.success(true) : Result.error("加入失败");
    }
    
    @PostMapping("/{groupId}/leave")
    public Result<Boolean> leaveGroup(
            @PathVariable Long groupId,
            @RequestBody LikeRequest request) {
        boolean success = groupService.leaveGroup(groupId, request.getUserId());
        return success ? Result.success(true) : Result.error("退出失败");
    }
    
    @GetMapping("/{groupId}/members")
    public Result<List<GroupMember>> getGroupMembers(@PathVariable Long groupId) {
        List<GroupMember> members = groupService.getGroupMembers(groupId);
        return Result.success(members);
    }
    
    @GetMapping("/{groupId}/check")
    public Result<Map<String, Object>> checkUserInGroup(
            @PathVariable Long groupId,
            @RequestParam Long userId) {
        boolean isMember = groupService.checkUserInGroup(groupId, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("isMember", isMember);
        return Result.success(result);
    }
}
