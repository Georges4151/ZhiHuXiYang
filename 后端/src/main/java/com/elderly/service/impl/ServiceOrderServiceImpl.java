package com.elderly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.entity.ServiceOrder;
import com.elderly.mapper.ServiceOrderMapper;
import com.elderly.service.ServiceOrderService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ServiceOrderServiceImpl extends ServiceImpl<ServiceOrderMapper, ServiceOrder> implements ServiceOrderService {
    
    @Override
    public Page<ServiceOrder> getPageByUserId(Long userId, Integer pageNum, Integer pageSize) {
        return this.page(new Page<>(pageNum, pageSize), new LambdaQueryWrapper<ServiceOrder>()
                .eq(ServiceOrder::getUserId, userId)
                .orderByDesc(ServiceOrder::getCreateTime));
    }
    
    @Override
    public ServiceOrder createOrder(ServiceOrder order) {
        order.setOrderNo(generateOrderNo());
        order.setOrderStatus(0);
        order.setPayStatus(0);
        this.save(order);
        return order;
    }
    
    @Override
    public boolean updateOrderStatus(Long orderId, Integer status) {
        ServiceOrder order = this.getById(orderId);
        if (order != null) {
            order.setOrderStatus(status);
            return this.updateById(order);
        }
        return false;
    }
    
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "SV" + timestamp + random;
    }
}
