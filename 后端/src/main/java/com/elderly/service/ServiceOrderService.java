package com.elderly.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.entity.ServiceOrder;

public interface ServiceOrderService extends IService<ServiceOrder> {
    
    Page<ServiceOrder> getPageByUserId(Long userId, Integer pageNum, Integer pageSize);
    
    ServiceOrder createOrder(ServiceOrder order);
    
    boolean updateOrderStatus(Long orderId, Integer status);
}
