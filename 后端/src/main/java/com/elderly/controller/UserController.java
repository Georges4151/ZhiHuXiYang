package com.elderly.controller;

import com.elderly.common.Result;
import com.elderly.entity.ElderlyUser;
import com.elderly.service.ElderlyUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private ElderlyUserService userService;
    
    @Value("${file.upload-path}")
    private String uploadPath;
    
    @PostMapping("/login")
    public Result<ElderlyUser> login(@RequestBody ElderlyUser user) {
        ElderlyUser existUser = userService.getByOpenId(user.getOpenId());
        if (existUser == null) {
            existUser = userService.loginOrRegister(user.getOpenId(), user.getName(), user.getAvatar());
        }
        return Result.success(existUser);
    }
    
    @GetMapping("/info/{id}")
    public Result<ElderlyUser> getUserInfo(@PathVariable Long id) {
        ElderlyUser user = userService.getById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }
    
    @GetMapping("/profile/{id}")
    public Result<Map<String, Object>> getProfile(@PathVariable Long id) {
        ElderlyUser user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("name", user.getName());
        profile.put("phone", user.getPhone());
        profile.put("avatar", user.getAvatar());
        profile.put("gender", user.getGender());
        profile.put("genderText", user.getGender() == 1 ? "男" : (user.getGender() == 2 ? "女" : "未设置"));
        profile.put("birthday", user.getBirthday() != null ? user.getBirthday().toString() : null);
        profile.put("emergencyContact", user.getEmergencyContact());
        profile.put("emergencyPhone", user.getEmergencyPhone());
        profile.put("healthCondition", user.getHealthCondition());
        profile.put("address", user.getAddress());
        
        return Result.success(profile);
    }
    
    @PutMapping("/update")
    public Result<Boolean> updateUser(@RequestBody ElderlyUser user) {
        if (user.getId() == null) {
            return Result.error("用户ID不能为空");
        }
        
        ElderlyUser existUser = userService.getById(user.getId());
        if (existUser == null) {
            return Result.error("用户不存在");
        }
        
        if (user.getName() != null) {
            existUser.setName(user.getName());
        }
        if (user.getGender() != null) {
            existUser.setGender(user.getGender());
        }
        if (user.getBirthday() != null) {
            existUser.setBirthday(user.getBirthday());
        }
        if (user.getEmergencyContact() != null) {
            existUser.setEmergencyContact(user.getEmergencyContact());
        }
        if (user.getEmergencyPhone() != null) {
            existUser.setEmergencyPhone(user.getEmergencyPhone());
        }
        if (user.getHealthCondition() != null) {
            existUser.setHealthCondition(user.getHealthCondition());
        }
        if (user.getAddress() != null) {
            existUser.setAddress(user.getAddress());
        }
        if (user.getAvatar() != null) {
            existUser.setAvatar(user.getAvatar());
        }
        
        boolean success = userService.updateById(existUser);
        return success ? Result.success() : Result.error("更新失败");
    }
    
    @PostMapping("/avatar")
    public Result<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file,
                                                     @RequestParam("userId") Long userId) {
        if (file.isEmpty()) {
            return Result.error("请选择文件");
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return Result.error("文件名无效");
        }
        
        String extension = "";
        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex > 0) {
            extension = originalFilename.substring(lastDotIndex);
        }
        
        if (!extension.matches("\\.(jpg|jpeg|png|gif|webp)$")) {
            return Result.error("只支持jpg、png、gif、webp格式的图片");
        }
        
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
        String relativePath = "avatar/" + datePath + "/" + fileName;
        
        File destFile = new File(uploadPath + relativePath);
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        
        try {
            file.transferTo(destFile);
            
            String avatarUrl = "/upload/" + relativePath;
            
            ElderlyUser user = userService.getById(userId);
            if (user != null) {
                user.setAvatar(avatarUrl);
                userService.updateById(user);
            }
            
            Map<String, String> result = new HashMap<>();
            result.put("url", avatarUrl);
            return Result.success(result);
            
        } catch (IOException e) {
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }
    
    @PutMapping("/password")
    public Result<Boolean> updatePassword(@RequestBody Map<String, Object> params) {
        Long userId = params.get("userId") != null ? Long.valueOf(params.get("userId").toString()) : null;
        String oldPassword = (String) params.get("oldPassword");
        String newPassword = (String) params.get("newPassword");
        
        if (userId == null || newPassword == null) {
            return Result.error("参数不完整");
        }
        
        ElderlyUser user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            if (oldPassword == null || !user.getPassword().equals(oldPassword)) {
                return Result.error("原密码错误");
            }
        }
        
        user.setPassword(newPassword);
        boolean success = userService.updateById(user);
        return success ? Result.success() : Result.error("密码修改失败");
    }
    
    @GetMapping("/search")
    public Result<List<Map<String, Object>>> searchUser(@RequestParam String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return Result.error("手机号不能为空");
        }
        
        ElderlyUser user = userService.getByPhone(phone.trim());
        if (user == null) {
            return Result.success(new ArrayList<>());
        }
        
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("name", user.getName());
        userMap.put("phone", user.getPhone());
        userMap.put("avatar", user.getAvatar());
        userMap.put("gender", user.getGender());
        result.add(userMap);
        
        return Result.success(result);
    }
}
