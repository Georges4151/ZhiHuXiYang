package com.elderly.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.elderly.entity.ActivityRegistration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ActivityRegistrationMapper extends BaseMapper<ActivityRegistration> {
    
    @Select("SELECT COUNT(*) FROM activity_registration WHERE activity_id = #{activityId} AND user_id = #{userId} AND deleted = 0")
    int checkUserRegistered(@Param("activityId") Long activityId, @Param("userId") Long userId);
}
