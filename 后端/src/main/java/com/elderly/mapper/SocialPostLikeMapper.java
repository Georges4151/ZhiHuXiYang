package com.elderly.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.elderly.entity.SocialPostLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SocialPostLikeMapper extends BaseMapper<SocialPostLike> {
    
    @Select("SELECT COUNT(*) FROM social_post_like WHERE post_id = #{postId} AND user_id = #{userId} AND deleted = 0")
    int checkUserLiked(@Param("postId") Long postId, @Param("userId") Long userId);
}
