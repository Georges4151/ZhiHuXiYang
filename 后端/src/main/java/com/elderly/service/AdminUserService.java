package com.elderly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.entity.AdminUser;

public interface AdminUserService extends IService<AdminUser> {
    
    AdminUser login(String username, String password);
    
    boolean changePassword(Long userId, String oldPassword, String newPassword);
    
    boolean resetPassword(Long userId, String newPassword);
}
