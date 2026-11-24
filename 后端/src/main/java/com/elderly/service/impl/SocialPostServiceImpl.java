package com.elderly.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.entity.ElderlyUser;
import com.elderly.entity.SocialPost;
import com.elderly.entity.SocialPostComment;
import com.elderly.entity.SocialPostLike;
import com.elderly.mapper.ElderlyUserMapper;
import com.elderly.mapper.SocialPostCommentMapper;
import com.elderly.mapper.SocialPostLikeMapper;
import com.elderly.mapper.SocialPostMapper;
import com.elderly.service.SocialPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SocialPostServiceImpl extends ServiceImpl<SocialPostMapper, SocialPost> implements SocialPostService {
    
    @Autowired
    private SocialPostLikeMapper likeMapper;
    
    @Autowired
    private SocialPostCommentMapper commentMapper;
    
    @Autowired
    private ElderlyUserMapper userMapper;
    
    @Override
    public List<SocialPost> getPostList(Integer category, Integer page, Integer size) {
        var query = lambdaQuery();
        // 当category不为null且大于0时，按分类筛选
        if (category != null && category > 0) {
            query.eq(SocialPost::getCategory, category);
        }
        return query.orderByDesc(SocialPost::getCreatedAt)
                .last("LIMIT " + (page - 1) * size + "," + size)
                .list();
    }
    
    @Override
    public SocialPost getPostDetail(Long postId) {
        return getById(postId);
    }
    
    @Override
    @Transactional
    public boolean createPost(Long userId, String userName, String userAvatar, String content, String images, Integer category) {
        System.out.println("=== 创建帖子开始 ===");
        System.out.println("userId: " + userId);
        System.out.println("userName: " + userName);
        System.out.println("userAvatar: " + userAvatar);
        System.out.println("content: " + content);
        System.out.println("images: " + images);
        System.out.println("category: " + category);
        
        SocialPost post = new SocialPost();
        post.setUserId(userId);
        post.setUserName(userName);
        post.setUserAvatar(userAvatar);
        post.setContent(content);
        post.setImages(images);
        post.setCategory(category);
        post.setLikes(0);
        post.setComments(0);
        
        System.out.println("=== 创建帖子结束 ===");
        return save(post);
    }
    
    @Override
    @Transactional
    public boolean likePost(Long postId, Long userId) {
        // 检查是否已经点赞
        if (likeMapper.checkUserLiked(postId, userId) > 0) {
            return true;
        }
        
        SocialPostLike like = new SocialPostLike();
        like.setPostId(postId);
        like.setUserId(userId);
        likeMapper.insert(like);
        
        // 更新点赞数
        SocialPost post = getById(postId);
        if (post != null) {
            post.setLikes(post.getLikes() + 1);
            updateById(post);
        }
        
        return true;
    }
    
    @Override
    @Transactional
    public boolean unlikePost(Long postId, Long userId) {
        // 删除点赞记录
        likeMapper.delete(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SocialPostLike>()
                .eq(SocialPostLike::getPostId, postId)
                .eq(SocialPostLike::getUserId, userId)
        );
        
        // 更新点赞数
        SocialPost post = getById(postId);
        if (post != null && post.getLikes() > 0) {
            post.setLikes(post.getLikes() - 1);
            updateById(post);
        }
        
        return true;
    }
    
    @Override
    @Transactional
    public boolean addComment(Long postId, Long userId, String content, Long parentId) {
        SocialPostComment comment = new SocialPostComment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(content);
        comment.setParentId(parentId);
        commentMapper.insert(comment);
        
        // 更新评论数
        SocialPost post = getById(postId);
        if (post != null) {
            post.setComments(post.getComments() + 1);
            updateById(post);
        }
        
        return true;
    }
    
    @Override
    public List<Map<String, Object>> getComments(Long postId) {
        List<SocialPostComment> comments = commentMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SocialPostComment>()
                .eq(SocialPostComment::getPostId, postId)
                .orderByDesc(SocialPostComment::getCreatedAt)
        );
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (SocialPostComment comment : comments) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", comment.getId());
            map.put("userId", comment.getUserId());
            map.put("userName", comment.getUserName());
            map.put("userAvatar", comment.getUserAvatar());
            map.put("content", comment.getContent());
            map.put("parentId", comment.getParentId());
            map.put("createdAt", comment.getCreatedAt());
            result.add(map);
        }
        
        return result;
    }
    
    @Override
    @Transactional
    public boolean deletePost(Long postId, Long userId) {
        SocialPost post = getById(postId);
        if (post == null || !post.getUserId().equals(userId)) {
            return false;
        }
        
        // 删除相关的点赞和评论
        likeMapper.delete(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SocialPostLike>()
                .eq(SocialPostLike::getPostId, postId)
        );
        
        commentMapper.delete(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SocialPostComment>()
                .eq(SocialPostComment::getPostId, postId)
        );
        
        return removeById(postId);
    }
}
