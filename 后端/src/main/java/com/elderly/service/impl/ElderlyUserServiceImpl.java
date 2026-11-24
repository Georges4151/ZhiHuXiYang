package com.elderly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.entity.ElderlyUser;
import com.elderly.mapper.ElderlyUserMapper;
import com.elderly.service.ElderlyUserService;
import org.springframework.stereotype.Service;

@Service
public class ElderlyUserServiceImpl extends ServiceImpl<ElderlyUserMapper, ElderlyUser> implements ElderlyUserService {
    
    @Override
    public ElderlyUser getByOpenId(String openId) {
        return this.getOne(new LambdaQueryWrapper<ElderlyUser>()
                .eq(ElderlyUser::getOpenId, openId));
    }
    
    @Override
    public ElderlyUser getByPhone(String phone) {
        return this.getOne(new LambdaQueryWrapper<ElderlyUser>()
                .eq(ElderlyUser::getPhone, phone));
    }
    
    @Override
    public ElderlyUser loginOrRegister(String openId, String name, String avatar) {
        ElderlyUser user = getByOpenId(openId);
        if (user == null) {
            user = new ElderlyUser();
            user.setOpenId(openId);
            user.setName(name);
            user.setAvatar(avatar);
            user.setUserType(1);
            user.setStatus(1);
            this.save(user);
        }
        return user;
    }
}
