package com.elderly.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.entity.GroupMember;
import com.elderly.entity.InterestGroup;
import com.elderly.mapper.GroupMemberMapper;
import com.elderly.mapper.InterestGroupMapper;
import com.elderly.service.InterestGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InterestGroupServiceImpl extends ServiceImpl<InterestGroupMapper, InterestGroup> implements InterestGroupService {
    
    @Autowired
    private GroupMemberMapper memberMapper;
    
    @Override
    public List<InterestGroup> getGroupList(Integer category, Integer page, Integer size) {
        return lambdaQuery()
                .eq(category != null, InterestGroup::getCategory, category)
                .eq(InterestGroup::getStatus, 1)
                .orderByDesc(InterestGroup::getMemberCount)
                .last("LIMIT " + (page - 1) * size + "," + size)
                .list();
    }
    
    @Override
    public InterestGroup getGroupDetail(Long groupId) {
        return getById(groupId);
    }
    
    @Override
    @Transactional
    public boolean createGroup(Long userId, String name, String description, String coverImage, Integer category) {
        InterestGroup group = new InterestGroup();
        group.setName(name);
        group.setDescription(description);
        group.setCoverImage(coverImage);
        group.setCategory(category);
        group.setCreatorId(userId);
        group.setMemberCount(1);
        group.setPostCount(0);
        group.setStatus(1);
        
        boolean success = save(group);
        
        if (success) {
            // 创建者自动加入群组
            GroupMember member = new GroupMember();
            member.setGroupId(group.getId());
            member.setUserId(userId);
            member.setRole(1); // 群主
            memberMapper.insert(member);
        }
        
        return success;
    }
    
    @Override
    @Transactional
    public boolean joinGroup(Long groupId, Long userId, String userName, String userAvatar) {
        // 检查是否已在群组中
        if (checkUserInGroup(groupId, userId)) {
            return true;
        }
        
        GroupMember member = new GroupMember();
        member.setGroupId(groupId);
        member.setUserId(userId);
        member.setUserName(userName);
        member.setUserAvatar(userAvatar);
        member.setRole(0); // 普通成员
        memberMapper.insert(member);
        
        // 更新成员数
        InterestGroup group = getById(groupId);
        if (group != null) {
            group.setMemberCount(group.getMemberCount() + 1);
            updateById(group);
        }
        
        return true;
    }
    
    @Override
    @Transactional
    public boolean leaveGroup(Long groupId, Long userId) {
        memberMapper.delete(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<GroupMember>()
                .eq(GroupMember::getGroupId, groupId)
                .eq(GroupMember::getUserId, userId)
        );
        
        // 更新成员数
        InterestGroup group = getById(groupId);
        if (group != null && group.getMemberCount() > 0) {
            group.setMemberCount(group.getMemberCount() - 1);
            updateById(group);
        }
        
        return true;
    }
    
    @Override
    public boolean checkUserInGroup(Long groupId, Long userId) {
        return memberMapper.checkUserInGroup(groupId, userId) > 0;
    }
    
    @Override
    public List<GroupMember> getGroupMembers(Long groupId) {
        return memberMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<GroupMember>()
                .eq(GroupMember::getGroupId, groupId)
                .orderByDesc(GroupMember::getRole)
        );
    }
}
