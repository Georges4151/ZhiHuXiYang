package com.elderly.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.common.Result;
import com.elderly.entity.ServiceItem;
import com.elderly.mapper.ServiceItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/service")
public class AdminServiceController {

    @Autowired
    private ServiceItemMapper serviceItemMapper;

    @GetMapping("/list")
    public Result<Map<String, Object>> getServiceList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer status) {
        
        Page<ServiceItem> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<ServiceItem> wrapper = new LambdaQueryWrapper<>();
        
        if (name != null && !name.isEmpty()) {
            wrapper.like(ServiceItem::getName, name);
        }
        if (status != null) {
            wrapper.eq(ServiceItem::getStatus, status);
        }
        wrapper.eq(ServiceItem::getDeleted, 0);
        wrapper.orderByDesc(ServiceItem::getCreateTime);
        
        Page<ServiceItem> result = serviceItemMapper.selectPage(pageParam, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("list", result.getRecords());
        data.put("total", result.getTotal());
        data.put("page", page);
        data.put("pageSize", pageSize);
        
        return Result.success(data);
    }

    @GetMapping("/{id}")
    public Result<ServiceItem> getServiceDetail(@PathVariable Long id) {
        ServiceItem item = serviceItemMapper.selectById(id);
        if (item == null) {
            return Result.error("服务不存在");
        }
        return Result.success(item);
    }

    @PostMapping
    public Result<Boolean> addService(@RequestBody ServiceItem item) {
        int rows = serviceItemMapper.insert(item);
        return rows > 0 ? Result.success(true) : Result.error("添加失败");
    }

    @PutMapping("/{id}")
    public Result<Boolean> updateService(@PathVariable Long id, @RequestBody ServiceItem item) {
        item.setId(id);
        int rows = serviceItemMapper.updateById(item);
        return rows > 0 ? Result.success(true) : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteService(@PathVariable Long id) {
        LambdaUpdateWrapper<ServiceItem> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ServiceItem::getId, id);
        wrapper.set(ServiceItem::getDeleted, 1);
        int rows = serviceItemMapper.update(null, wrapper);
        return rows > 0 ? Result.success(true) : Result.error("删除失败");
    }
}
