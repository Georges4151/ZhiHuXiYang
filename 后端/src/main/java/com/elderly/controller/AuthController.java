package com.elderly.controller;

import com.elderly.common.Result;
import com.elderly.dto.LoginRequest;
import com.elderly.dto.RegisterRequest;
import com.elderly.entity.ElderlyUser;
import com.elderly.service.AuthService;
import com.elderly.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody @Validated LoginRequest request) {
        ElderlyUser user = authService.login(request);
        // 生成 JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getPhone(), "user");
        
        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("token", token);
        
        return Result.success("登录成功", result);
    }
    
    @PostMapping("/register")
    public Result<ElderlyUser> register(@RequestBody @Validated RegisterRequest request) {
        ElderlyUser user = authService.register(request);
        return Result.success("注册成功", user);
    }
}
