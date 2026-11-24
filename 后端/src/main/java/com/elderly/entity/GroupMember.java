package com.elderly.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("group_member")
public class GroupMember {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long groupId;
    
    private Long userId;
    
    private String userName;
    
    private String userAvatar;
    
    private Integer role;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime joinedAt;
    
    @TableLogic
    private Integer deleted;
}
