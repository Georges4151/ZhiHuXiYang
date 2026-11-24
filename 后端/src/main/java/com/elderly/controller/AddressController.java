package com.elderly.controller;

import com.elderly.common.Result;
import com.elderly.entity.Address;
import com.elderly.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/address")
public class AddressController {
    
    @Autowired
    private AddressService addressService;
    
    @GetMapping("/list/{userId}")
    public Result<List<Address>> getList(@PathVariable Long userId) {
        List<Address> list = addressService.getByUserId(userId);
        return Result.success(list);
    }
    
    @GetMapping("/default/{userId}")
    public Result<Address> getDefault(@PathVariable Long userId) {
        Address address = addressService.getDefaultByUserId(userId);
        return Result.success(address);
    }
    
    @GetMapping("/detail/{id}")
    public Result<Address> getDetail(@PathVariable Long id) {
        Address address = addressService.getById(id);
        return Result.success(address);
    }
    
    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody Address address) {
        boolean success = addressService.addAddress(address);
        return Result.success(success);
    }
    
    @PostMapping("/update")
    public Result<Boolean> update(@RequestBody Address address) {
        boolean success = addressService.updateAddress(address);
        return Result.success(success);
    }
    
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean success = addressService.deleteAddress(id);
        return Result.success(success);
    }
    
    @PutMapping("/setDefault/{userId}/{addressId}")
    public Result<Boolean> setDefault(@PathVariable Long userId, @PathVariable Long addressId) {
        boolean success = addressService.setDefault(userId, addressId);
        return Result.success(success);
    }
}
