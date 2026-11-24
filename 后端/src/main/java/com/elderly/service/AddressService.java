package com.elderly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.entity.Address;

import java.util.List;

public interface AddressService extends IService<Address> {
    
    List<Address> getByUserId(Long userId);
    
    Address getDefaultByUserId(Long userId);
    
    boolean addAddress(Address address);
    
    boolean updateAddress(Address address);
    
    boolean deleteAddress(Long id);
    
    boolean setDefault(Long userId, Long addressId);
}
