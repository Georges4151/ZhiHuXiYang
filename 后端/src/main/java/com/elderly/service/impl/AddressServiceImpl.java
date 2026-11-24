package com.elderly.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.entity.Address;
import com.elderly.mapper.AddressMapper;
import com.elderly.service.AddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {
    
    @Override
    public List<Address> getByUserId(Long userId) {
        return lambdaQuery()
                .eq(Address::getUserId, userId)
                .orderByDesc(Address::getIsDefault)
                .orderByDesc(Address::getCreateTime)
                .list();
    }
    
    @Override
    public Address getDefaultByUserId(Long userId) {
        return lambdaQuery()
                .eq(Address::getUserId, userId)
                .eq(Address::getIsDefault, 1)
                .one();
    }
    
    @Override
    public boolean addAddress(Address address) {
        // 参数校验
        if (address == null) {
            return false;
        }
        if (address.getUserId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (address.getName() == null || address.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("收货人姓名不能为空");
        }
        if (address.getPhone() == null || address.getPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("联系电话不能为空");
        }
        if (address.getDetail() == null || address.getDetail().trim().isEmpty()) {
            throw new IllegalArgumentException("详细地址不能为空");
        }
        // 设置默认值
        if (address.getIsDefault() == null) {
            address.setIsDefault(0);
        }
        if (address.getIsDefault() == 1) {
            clearDefault(address.getUserId());
        }
        return save(address);
    }
    
    @Override
    public boolean updateAddress(Address address) {
        // 参数校验
        if (address == null || address.getId() == null) {
            return false;
        }
        if (address.getName() != null && address.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("收货人姓名不能为空");
        }
        if (address.getPhone() != null && address.getPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("联系电话不能为空");
        }
        if (address.getDetail() != null && address.getDetail().trim().isEmpty()) {
            throw new IllegalArgumentException("详细地址不能为空");
        }
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            clearDefault(address.getUserId());
        }
        return updateById(address);
    }
    
    @Override
    public boolean deleteAddress(Long id) {
        return removeById(id);
    }
    
    @Override
    @Transactional
    public boolean setDefault(Long userId, Long addressId) {
        clearDefault(userId);
        
        Address address = getById(addressId);
        if (address != null) {
            address.setIsDefault(1);
            return updateById(address);
        }
        return false;
    }
    
    private void clearDefault(Long userId) {
        lambdaUpdate()
                .eq(Address::getUserId, userId)
                .set(Address::getIsDefault, 0)
                .update();
    }
}
