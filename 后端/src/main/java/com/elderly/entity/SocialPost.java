package com.elderly.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("social_post")
public class SocialPost {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String userName;
    
    private String userAvatar;
    
    private String content;
    
    private String images;
    
    private Integer likes;
    
    private Integer comments;
    
    private Integer category;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic
    private Integer deleted;
}
