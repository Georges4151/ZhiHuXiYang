package com.elderly.controller;

import com.elderly.common.Result;
import com.elderly.dto.CreatePostRequest;
import com.elderly.dto.LikeRequest;
import com.elderly.dto.CommentRequest;
import com.elderly.entity.SocialPost;
import com.elderly.service.SocialPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/social")
public class SocialController {
    
    @Autowired
    private SocialPostService socialPostService;
    
    @GetMapping("/posts")
    public Result<List<SocialPost>> getPostList(
            @RequestParam(required = false) Integer category,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        List<SocialPost> posts = socialPostService.getPostList(category, page, size);
        return Result.success(posts);
    }
    
    @GetMapping("/posts/{postId}")
    public Result<SocialPost> getPostDetail(@PathVariable Long postId) {
        SocialPost post = socialPostService.getPostDetail(postId);
        if (post == null) {
            return Result.error("动态不存在");
        }
        return Result.success(post);
    }
    
    @PostMapping("/posts")
    public Result<Boolean> createPost(@RequestBody CreatePostRequest request) {
        boolean success = socialPostService.createPost(
            request.getUserId(), 
            request.getUserName(),
            request.getUserAvatar(),
            request.getContent(), 
            request.getImages(), 
            request.getCategory()
        );
        return success ? Result.success(true) : Result.error("发布失败");
    }
    
    @PostMapping("/posts/{postId}/like")
    public Result<Boolean> likePost(
            @PathVariable Long postId,
            @RequestBody LikeRequest request) {
        boolean success = socialPostService.likePost(postId, request.getUserId());
        return success ? Result.success(true) : Result.error("点赞失败");
    }
    
    @PostMapping("/posts/{postId}/unlike")
    public Result<Boolean> unlikePost(
            @PathVariable Long postId,
            @RequestBody LikeRequest request) {
        boolean success = socialPostService.unlikePost(postId, request.getUserId());
        return success ? Result.success(true) : Result.error("取消点赞失败");
    }
    
    @PostMapping("/posts/{postId}/comments")
    public Result<Boolean> addComment(
            @PathVariable Long postId,
            @RequestBody CommentRequest request) {
        boolean success = socialPostService.addComment(
            postId, 
            request.getUserId(), 
            request.getContent(), 
            request.getParentId()
        );
        return success ? Result.success(true) : Result.error("评论失败");
    }
    
    @GetMapping("/posts/{postId}/comments")
    public Result<List<Map<String, Object>>> getComments(@PathVariable Long postId) {
        List<Map<String, Object>> comments = socialPostService.getComments(postId);
        return Result.success(comments);
    }
    
    @DeleteMapping("/posts/{postId}")
    public Result<Boolean> deletePost(
            @PathVariable Long postId,
            @RequestBody LikeRequest request) {
        boolean success = socialPostService.deletePost(postId, request.getUserId());
        return success ? Result.success(true) : Result.error("删除失败或无权限");
    }
}
