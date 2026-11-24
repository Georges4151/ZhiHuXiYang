package com.elderly.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.entity.AdminUser;
import com.elderly.mapper.AdminUserMapper;
import com.elderly.service.AdminUserService;
import com.elderly.util.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class AdminUserServiceImpl extends ServiceImpl<AdminUserMapper, AdminUser> implements AdminUserService {

    @Override
    public AdminUser login(String username, String password) {
        AdminUser user = lambdaQuery()
                .eq(AdminUser::getUsername, username)
                .eq(AdminUser::getStatus, 1)
                .one();
        
        if (user == null) {
            return null;
        }
        
        boolean passwordMatches;
        if (user.getPassword().startsWith("$2a$")) {
            passwordMatches = BCrypt.checkpw(password, user.getPassword());
        } else {
            passwordMatches = password.equals(user.getPassword());
        }
        
        return passwordMatches ? user : null;
    }

    @Override
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        AdminUser user = getById(userId);
        if (user == null) {
            return false;
        }
        
        boolean oldPasswordMatches;
        if (user.getPassword().startsWith("$2a$")) {
            oldPasswordMatches = BCrypt.checkpw(oldPassword, user.getPassword());
        } else {
            oldPasswordMatches = oldPassword.equals(user.getPassword());
        }
        
        if (!oldPasswordMatches) {
            return false;
        }
        
        user.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        return updateById(user);
    }

    @Override
    public boolean resetPassword(Long userId, String newPassword) {
        AdminUser user = getById(userId);
        if (user == null) {
            return false;
        }
        user.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        return updateById(user);
    }
}
