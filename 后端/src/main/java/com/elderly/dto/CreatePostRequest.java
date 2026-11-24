package com.elderly.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Data
public class CreatePostRequest {
    
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    private String userName;
    
    private String userAvatar;
    
    private String content;
    
    private String images;
    
    private Integer category = 0;
}
