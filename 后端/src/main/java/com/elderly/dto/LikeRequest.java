package com.elderly.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class LikeRequest {
    
    @NotNull(message = "用户ID不能为空")
    private Long userId;
}
