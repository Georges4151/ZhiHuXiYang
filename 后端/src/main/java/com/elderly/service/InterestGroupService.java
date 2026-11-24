package com.elderly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.entity.GroupMember;
import com.elderly.entity.InterestGroup;

import java.util.List;

public interface InterestGroupService extends IService<InterestGroup> {
    
    List<InterestGroup> getGroupList(Integer category, Integer page, Integer size);
    
    InterestGroup getGroupDetail(Long groupId);
    
    boolean createGroup(Long userId, String name, String description, String coverImage, Integer category);
    
    boolean joinGroup(Long groupId, Long userId, String userName, String userAvatar);
    
    boolean leaveGroup(Long groupId, Long userId);
    
    boolean checkUserInGroup(Long groupId, Long userId);
    
    List<GroupMember> getGroupMembers(Long groupId);
}
