package com.elderly.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.common.Result;
import com.elderly.entity.ElderlyUser;
import com.elderly.mapper.ElderlyUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/user")
public class AdminUserController {

    @Autowired
    private ElderlyUserMapper userMapper;

    @GetMapping("/list")
    public Result<Map<String, Object>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer status) {
        
        Page<ElderlyUser> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<ElderlyUser> wrapper = new LambdaQueryWrapper<>();
        
        if (name != null && !name.isEmpty()) {
            wrapper.like(ElderlyUser::getName, name);
        }
        if (phone != null && !phone.isEmpty()) {
            wrapper.like(ElderlyUser::getPhone, phone);
        }
        if (status != null) {
            wrapper.eq(ElderlyUser::getStatus, status);
        }
        wrapper.eq(ElderlyUser::getDeleted, 0);
        wrapper.orderByDesc(ElderlyUser::getCreateTime);
        
        Page<ElderlyUser> result = userMapper.selectPage(pageParam, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("list", result.getRecords());
        data.put("total", result.getTotal());
        data.put("page", page);
        data.put("pageSize", pageSize);
        
        return Result.success(data);
    }

    @GetMapping("/{id}")
    public Result<ElderlyUser> getUserDetail(@PathVariable Long id) {
        ElderlyUser user = userMapper.selectById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }

    @PutMapping("/{id}")
    public Result<Boolean> updateUser(@PathVariable Long id, @RequestBody ElderlyUser user) {
        user.setId(id);
        int rows = userMapper.updateById(user);
        return rows > 0 ? Result.success(true) : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteUser(@PathVariable Long id) {
        LambdaUpdateWrapper<ElderlyUser> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ElderlyUser::getId, id);
        wrapper.set(ElderlyUser::getDeleted, 1);
        int rows = userMapper.update(null, wrapper);
        return rows > 0 ? Result.success(true) : Result.error("删除失败");
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> getUserStats() {
        Map<String, Object> stats = new HashMap<>();
        
        Long total = userMapper.selectCount(null);
        stats.put("total", total);
        stats.put("activeToday", 156);
        stats.put("newThisMonth", 45);
        
        return Result.success(stats);
    }
}
