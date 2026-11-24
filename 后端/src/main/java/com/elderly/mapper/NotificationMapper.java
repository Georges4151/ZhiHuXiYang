package com.elderly.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.elderly.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
    
    @Select("SELECT * FROM notification WHERE user_id = #{userId} AND deleted = 0 ORDER BY create_time DESC LIMIT #{limit}")
    List<Notification> selectRecentByUserId(@Param("userId") Long userId, @Param("limit") int limit);
    
    @Select("SELECT * FROM notification WHERE user_id = #{userId} AND type = #{type} AND deleted = 0 ORDER BY create_time DESC")
    List<Notification> selectByUserIdAndType(@Param("userId") Long userId, @Param("type") Integer type);
    
    @Select("SELECT COUNT(*) FROM notification WHERE user_id = #{userId} AND is_read = 0 AND deleted = 0")
    int countUnread(@Param("userId") Long userId);
    
    @Update("UPDATE notification SET is_read = 1 WHERE user_id = #{userId} AND is_read = 0")
    int markAllRead(@Param("userId") Long userId);
}
