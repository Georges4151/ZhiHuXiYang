package com.elderly.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.elderly.common.Result;
import com.elderly.entity.ElderlyUser;
import com.elderly.entity.ServiceItem;
import com.elderly.entity.ServiceOrder;
import com.elderly.mapper.ElderlyUserMapper;
import com.elderly.mapper.ServiceItemMapper;
import com.elderly.mapper.ServiceOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/admin/stats")
public class AdminStatsController {

    @Autowired
    private ElderlyUserMapper userMapper;

    @Autowired
    private ServiceOrderMapper orderMapper;

    @Autowired
    private ServiceItemMapper serviceItemMapper;

    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview() {
        Map<String, Object> overview = new HashMap<>();
        
        // 查询真实数据
        Long userCount = userMapper.selectCount(
            new LambdaQueryWrapper<ElderlyUser>().eq(ElderlyUser::getDeleted, 0)
        );
        Long orderCount = orderMapper.selectCount(
            new LambdaQueryWrapper<ServiceOrder>().eq(ServiceOrder::getDeleted, 0)
        );
        Long serviceCount = serviceItemMapper.selectCount(
            new LambdaQueryWrapper<ServiceItem>().eq(ServiceItem::getStatus, 1)
        );
        
        // 计算满意度（根据已完成的订单中有评价的订单计算平均分）
        List<ServiceOrder> completedOrders = orderMapper.selectList(
            new LambdaQueryWrapper<ServiceOrder>()
                .eq(ServiceOrder::getOrderStatus, 3)
                .isNotNull(ServiceOrder::getServiceRating)
        );
        
        double satisfactionRate = 95.0; // 默认值
        if (!completedOrders.isEmpty()) {
            double avgRating = completedOrders.stream()
                .mapToInt(ServiceOrder::getServiceRating)
                .average()
                .orElse(5.0);
            satisfactionRate = (avgRating / 5.0) * 100;
        }
        
        overview.put("userCount", userCount);
        overview.put("orderCount", orderCount);
        overview.put("serviceCount", serviceCount);
        overview.put("satisfactionRate", String.format("%.1f", satisfactionRate));
        
        return Result.success(overview);
    }

    @GetMapping("/order-trend")
    public Result<List<Map<String, Object>>> getOrderTrend(@RequestParam(defaultValue = "week") String type) {
        List<Map<String, Object>> trend = new ArrayList<>();
        
        // 获取最近7天的日期
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dayStr = date.format(formatter);
            
            // 查询当天的订单数
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
            
            Long dayOrderCount = orderMapper.selectCount(
                new LambdaQueryWrapper<ServiceOrder>()
                    .ge(ServiceOrder::getCreateTime, startOfDay)
                    .lt(ServiceOrder::getCreateTime, endOfDay)
            );
            
            // 查询当天完成的订单数
            Long dayCompletedCount = orderMapper.selectCount(
                new LambdaQueryWrapper<ServiceOrder>()
                    .ge(ServiceOrder::getCreateTime, startOfDay)
                    .lt(ServiceOrder::getCreateTime, endOfDay)
                    .eq(ServiceOrder::getOrderStatus, 3)
            );
            
            Map<String, Object> item = new HashMap<>();
            item.put("day", dayStr);
            item.put("orders", dayOrderCount.intValue());
            item.put("completed", dayCompletedCount.intValue());
            trend.add(item);
        }
        
        return Result.success(trend);
    }

    @GetMapping("/service-distribution")
    public Result<List<Map<String, Object>>> getServiceDistribution() {
        List<Map<String, Object>> distribution = new ArrayList<>();
        
        // 查询所有服务项目的订单数量分布
        List<ServiceItem> services = serviceItemMapper.selectList(
            new LambdaQueryWrapper<ServiceItem>().eq(ServiceItem::getStatus, 1)
        );
        
        for (ServiceItem service : services) {
            Long count = orderMapper.selectCount(
                new LambdaQueryWrapper<ServiceOrder>()
                    .eq(ServiceOrder::getServiceId, service.getId())
            );
            
            if (count > 0) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", service.getName());
                item.put("value", count.intValue());
                distribution.add(item);
            }
        }
        
        // 如果没有数据，返回默认数据
        if (distribution.isEmpty()) {
            String[] defaultServices = {"家政服务", "医疗陪护", "送餐服务", "康复理疗", "心理咨询"};
            int[] defaultValues = {10, 8, 6, 4, 2};
            
            for (int i = 0; i < defaultServices.length; i++) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", defaultServices[i]);
                item.put("value", defaultValues[i]);
                distribution.add(item);
            }
        }
        
        return Result.success(distribution);
    }

    @GetMapping("/recent-orders")
    public Result<List<Map<String, Object>>> getRecentOrders() {
        List<Map<String, Object>> orders = new ArrayList<>();
        
        // 查询最近5条订单
        List<ServiceOrder> recentOrders = orderMapper.selectList(
            new LambdaQueryWrapper<ServiceOrder>()
                .orderByDesc(ServiceOrder::getCreateTime)
                .last("LIMIT 5")
        );
        
        for (ServiceOrder order : recentOrders) {
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("orderNo", order.getOrderNo());
            orderMap.put("serviceName", order.getServiceName());
            orderMap.put("userName", order.getContactName() != null ? order.getContactName() : "未知用户");
            orderMap.put("status", order.getOrderStatus());
            orders.add(orderMap);
        }
        
        return Result.success(orders);
    }

    @GetMapping("/recent-users")
    public Result<List<Map<String, Object>>> getRecentUsers() {
        List<Map<String, Object>> users = new ArrayList<>();
        
        // 查询最近5条用户
        List<ElderlyUser> recentUsers = userMapper.selectList(
            new LambdaQueryWrapper<ElderlyUser>()
                .eq(ElderlyUser::getDeleted, 0)
                .orderByDesc(ElderlyUser::getCreateTime)
                .last("LIMIT 5")
        );
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (ElderlyUser user : recentUsers) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("name", user.getName() != null ? user.getName() : "未命名");
            userMap.put("phone", maskPhone(user.getPhone()));
            userMap.put("age", calculateAge(user.getBirthday()));
            userMap.put("createdAt", user.getCreateTime() != null ? 
                user.getCreateTime().format(formatter) : "未知");
            users.add(userMap);
        }
        
        return Result.success(users);
    }
    
    /**
     * 手机号脱敏
     */
    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return "***";
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }
    
    /**
     * 根据生日计算年龄
     */
    private int calculateAge(LocalDate birthday) {
        if (birthday == null) {
            return 70; // 默认年龄
        }
        return LocalDate.now().getYear() - birthday.getYear();
    }
}
