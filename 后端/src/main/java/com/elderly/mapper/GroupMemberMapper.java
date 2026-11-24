package com.elderly.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.elderly.entity.GroupMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface GroupMemberMapper extends BaseMapper<GroupMember> {
    
    @Select("SELECT COUNT(*) FROM group_member WHERE group_id = #{groupId} AND user_id = #{userId} AND deleted = 0")
    int checkUserInGroup(@Param("groupId") Long groupId, @Param("userId") Long userId);
}
