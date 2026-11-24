package com.elderly.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("activity_comment")
public class ActivityComment {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long activityId;
    
    private Long userId;
    
    private String userName;
    
    private String content;
    
    private Long parentId;
    
    private Long replyToUserId;
    
    private String replyToUserName;
    
    private Integer likes;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableLogic
    private Integer deleted;
}
