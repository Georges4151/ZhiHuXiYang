package com.elderly.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.elderly.entity.FamilyActivity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FamilyActivityMapper extends BaseMapper<FamilyActivity> {
    
    @Select("SELECT * FROM family_activity WHERE user_id = #{userId} AND deleted = 0 ORDER BY create_time DESC LIMIT #{limit}")
    List<FamilyActivity> selectRecentByUserId(@Param("userId") Long userId, @Param("limit") int limit);
}
