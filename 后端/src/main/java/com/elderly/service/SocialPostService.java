package com.elderly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.entity.SocialPost;

import java.util.List;
import java.util.Map;

public interface SocialPostService extends IService<SocialPost> {
    
    List<SocialPost> getPostList(Integer category, Integer page, Integer size);
    
    SocialPost getPostDetail(Long postId);
    
    boolean createPost(Long userId, String userName, String userAvatar, String content, String images, Integer category);
    
    boolean likePost(Long postId, Long userId);
    
    boolean unlikePost(Long postId, Long userId);
    
    boolean addComment(Long postId, Long userId, String content, Long parentId);
    
    List<Map<String, Object>> getComments(Long postId);
    
    boolean deletePost(Long postId, Long userId);
}
