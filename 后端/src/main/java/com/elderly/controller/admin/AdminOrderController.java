package com.elderly.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.common.Result;
import com.elderly.entity.ServiceOrder;
import com.elderly.mapper.ServiceOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/order")
public class AdminOrderController {

    @Autowired
    private ServiceOrderMapper orderMapper;

    @GetMapping("/list")
    public Result<Map<String, Object>> getOrderList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) Integer status) {
        
        Page<ServiceOrder> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<ServiceOrder> wrapper = new LambdaQueryWrapper<>();
        
        if (orderNo != null && !orderNo.isEmpty()) {
            wrapper.like(ServiceOrder::getOrderNo, orderNo);
        }
        if (status != null) {
            wrapper.eq(ServiceOrder::getOrderStatus, status);
        }
        wrapper.eq(ServiceOrder::getDeleted, 0);
        wrapper.orderByDesc(ServiceOrder::getCreateTime);
        
        Page<ServiceOrder> result = orderMapper.selectPage(pageParam, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("list", result.getRecords());
        data.put("total", result.getTotal());
        data.put("page", page);
        data.put("pageSize", pageSize);
        
        return Result.success(data);
    }

    @GetMapping("/{id}")
    public Result<ServiceOrder> getOrderDetail(@PathVariable Long id) {
        ServiceOrder order = orderMapper.selectById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        return Result.success(order);
    }

    @PutMapping("/{id}/status")
    public Result<Boolean> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        Integer status = params.get("status");
        ServiceOrder order = new ServiceOrder();
        order.setId(id);
        order.setOrderStatus(status);
        int rows = orderMapper.updateById(order);
        return rows > 0 ? Result.success(true) : Result.error("更新失败");
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> getOrderStats() {
        Map<String, Object> stats = new HashMap<>();
        
        Long total = orderMapper.selectCount(null);
        stats.put("total", total);
        stats.put("pending", 15);
        stats.put("processing", 8);
        stats.put("completed", 120);
        
        return Result.success(stats);
    }
}
