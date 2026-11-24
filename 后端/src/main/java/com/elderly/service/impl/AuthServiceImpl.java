package com.elderly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.elderly.dto.LoginRequest;
import com.elderly.dto.RegisterRequest;
import com.elderly.entity.ElderlyUser;
import com.elderly.mapper.ElderlyUserMapper;
import com.elderly.service.AuthService;
import com.elderly.util.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {
    
    @Autowired
    private ElderlyUserMapper userMapper;
    
    @Override
    public ElderlyUser login(LoginRequest request) {
        ElderlyUser user = userMapper.selectOne(new LambdaQueryWrapper<ElderlyUser>()
                .eq(ElderlyUser::getPhone, request.getPhone()));
        
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        boolean passwordMatches;
        if (user.getPassword().startsWith("$2a$")) {
            passwordMatches = BCrypt.checkpw(request.getPassword(), user.getPassword());
        } else {
            passwordMatches = request.getPassword().equals(user.getPassword());
        }
        
        if (!passwordMatches) {
            throw new RuntimeException("密码错误");
        }
        
        if (user.getStatus() != 1) {
            throw new RuntimeException("账号已被禁用");
        }
        
        return user;
    }
    
    @Override
    public ElderlyUser register(RegisterRequest request) {
        ElderlyUser existUser = userMapper.selectOne(new LambdaQueryWrapper<ElderlyUser>()
                .eq(ElderlyUser::getPhone, request.getPhone()));
        
        if (existUser != null) {
            throw new RuntimeException("该手机号已注册");
        }
        
        ElderlyUser user = new ElderlyUser();
        user.setPhone(request.getPhone());
        // 保存明文密码（临时方案，生产环境应使用加密）
        user.setPassword(request.getPassword());
        user.setName(request.getName());
        user.setGender(request.getGender());
        user.setEmergencyContact(request.getEmergencyContact());
        user.setEmergencyPhone(request.getEmergencyPhone());
        user.setUserType(1);
        user.setStatus(1);
        user.setAvatar("/images/icon/user.png");
        
        userMapper.insert(user);
        
        return user;
    }
}
