package com.elderly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.entity.ElderlyUser;

public interface ElderlyUserService extends IService<ElderlyUser> {
    
    ElderlyUser getByOpenId(String openId);
    
    ElderlyUser getByPhone(String phone);
    
    ElderlyUser loginOrRegister(String openId, String name, String avatar);
}
