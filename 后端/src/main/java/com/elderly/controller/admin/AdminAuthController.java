package com.elderly.controller.admin;

import com.elderly.common.Result;
import com.elderly.entity.AdminUser;
import com.elderly.service.AdminUserService;
import com.elderly.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/auth")
public class AdminAuthController {

    @Autowired
    private AdminUserService adminUserService;
    
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        
        // 支持默认账号
        if ("wxh".equals(username) && "123456".equals(password)) {
            Map<String, Object> data = new HashMap<>();
            // 使用JWT生成token，持久化存储
            String token = jwtUtil.generateToken(1L, username, "admin");
            data.put("token", token);
            data.put("role", "admin");
            data.put("userId", 1L);
            return Result.success(data);
        }
        
        // 从数据库验证
        AdminUser user = adminUserService.login(username, password);
        if (user != null) {
            Map<String, Object> data = new HashMap<>();
            // 使用JWT生成token，持久化存储
            String token = jwtUtil.generateToken(user.getId(), user.getUsername(), "admin");
            data.put("token", token);
            data.put("role", user.getRole());
            data.put("userId", user.getId());
            return Result.success(data);
        }
        
        return Result.error("用户名或密码错误");
    }

    @PostMapping("/logout")
    public Result<Boolean> logout() {
        return Result.success(true);
    }

    @GetMapping("/info")
    public Result<Map<String, Object>> getInfo(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || authHeader.isEmpty()) {
            return Result.error("未登录");
        }
        
        // 移除Bearer前缀
        String token = authHeader;
        if (authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }
        
        // 使用JWT验证token
        if (!jwtUtil.validateToken(token)) {
            return Result.error("登录已过期");
        }
        
        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return Result.error("登录已过期");
        }
        
        // 默认账号处理
        if (userId == 1L) {
            Map<String, Object> info = new HashMap<>();
            info.put("id", 1L);
            info.put("name", "管理员");
            info.put("username", "wxh");
            info.put("avatar", null);
            info.put("role", "admin");
            info.put("roleName", "管理人员");
            info.put("phone", null);
            info.put("email", null);
            return Result.success(info);
        }
        
        AdminUser user = adminUserService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        Map<String, Object> info = new HashMap<>();
        info.put("id", user.getId());
        info.put("name", user.getName());
        info.put("username", user.getUsername());
        info.put("avatar", user.getAvatar());
        info.put("role", user.getRole());
        info.put("roleName", "admin".equals(user.getRole()) ? "管理人员" : "运营成员");
        info.put("phone", user.getPhone());
        info.put("email", user.getEmail());
        return Result.success(info);
    }

    @PostMapping("/changePassword")
    public Result<Boolean> changePassword(@RequestBody Map<String, String> params) {
        Long userId = Long.valueOf(params.get("userId"));
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        
        boolean success = adminUserService.changePassword(userId, oldPassword, newPassword);
        if (success) {
            return Result.success(true);
        }
        return Result.error("原密码错误");
    }

    @PostMapping("/register")
    public Result<Boolean> register(@RequestBody AdminUser user) {
        // 检查用户名是否已存在
        AdminUser existUser = adminUserService.lambdaQuery()
                .eq(AdminUser::getUsername, user.getUsername())
                .one();
        if (existUser != null) {
            return Result.error("用户名已存在");
        }
        
        user.setStatus(1);
        boolean success = adminUserService.save(user);
        if (success) {
            return Result.success(true);
        }
        return Result.error("注册失败");
    }
}
