package com.elderly.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.common.Result;
import com.elderly.entity.ServiceItem;
import com.elderly.entity.ServiceOrder;
import com.elderly.service.ServiceItemService;
import com.elderly.service.ServiceOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service")
public class ServiceController {
    
    @Autowired
    private ServiceItemService serviceItemService;
    
    @Autowired
    private ServiceOrderService serviceOrderService;
    
    @GetMapping("/list")
    public Result<List<ServiceItem>> getServiceList(
            @RequestParam(required = false) Integer categoryId) {
        List<ServiceItem> list;
        if (categoryId != null) {
            list = serviceItemService.getByCategoryId(categoryId);
        } else {
            list = serviceItemService.list();
        }
        return Result.success(list);
    }
    
    @GetMapping("/hot")
    public Result<List<ServiceItem>> getHotServices(
            @RequestParam(defaultValue = "6") Integer limit) {
        List<ServiceItem> list = serviceItemService.getHotServices(limit);
        return Result.success(list);
    }
    
    @GetMapping("/detail/{id}")
    public Result<ServiceItem> getServiceDetail(@PathVariable Long id) {
        ServiceItem item = serviceItemService.getById(id);
        return Result.success(item);
    }
    
    @PostMapping("/order/create")
    public Result<ServiceOrder> createOrder(@RequestBody ServiceOrder order) {
        ServiceOrder created = serviceOrderService.createOrder(order);
        return Result.success(created);
    }
    
    @GetMapping("/order/user/{userId}")
    public Result<Page<ServiceOrder>> getUserOrders(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<ServiceOrder> page = serviceOrderService.getPageByUserId(userId, pageNum, pageSize);
        return Result.success(page);
    }
    
    @PutMapping("/order/status")
    public Result<Boolean> updateOrderStatus(
            @RequestParam Long orderId,
            @RequestParam Integer status) {
        boolean success = serviceOrderService.updateOrderStatus(orderId, status);
        return success ? Result.success() : Result.error("更新失败");
    }
    
    @GetMapping("/order/detail/{id}")
    public Result<ServiceOrder> getOrderDetail(@PathVariable Long id) {
        ServiceOrder order = serviceOrderService.getById(id);
        return Result.success(order);
    }
}
