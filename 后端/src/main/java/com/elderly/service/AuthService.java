package com.elderly.service;

import com.elderly.dto.LoginRequest;
import com.elderly.dto.RegisterRequest;
import com.elderly.entity.ElderlyUser;

public interface AuthService {
    
    ElderlyUser login(LoginRequest request);
    
    ElderlyUser register(RegisterRequest request);
}
