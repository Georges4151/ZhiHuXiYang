package com.elderly.controller;

import com.elderly.common.Result;
import com.elderly.entity.ServiceOrder;
import com.elderly.service.ServiceOrderService;
import com.elderly.service.WechatPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/pay")
public class PayController {
    
    @Autowired
    private WechatPayService wechatPayService;
    
    @Autowired
    private ServiceOrderService orderService;
    
    /**
     * 创建支付订单
     * @param orderNo 订单号
     * @param openId 用户openid
     * @return 支付参数
     */
    @PostMapping("/create")
    public Result<Map<String, String>> createPayOrder(
            @RequestParam String orderNo,
            @RequestParam String openId) {
        
        log.info("创建支付订单: orderNo={}, openId={}", orderNo, openId);
        
        // 查询订单信息
        ServiceOrder order = orderService.lambdaQuery()
                .eq(ServiceOrder::getOrderNo, orderNo)
                .eq(ServiceOrder::getPayStatus, 0) // 未支付
                .one();
        
        if (order == null) {
            return Result.error("订单不存在或已支付");
        }
        
        // 创建微信支付订单
        return wechatPayService.createPayOrder(
                orderNo,
                order.getAmount().doubleValue(),
                openId,
                order.getServiceName()
        );
    }
    
    /**
     * 微信支付回调
     * @param request HTTP请求
     * @return 处理结果
     */
    @PostMapping("/notify")
    public String payNotify(HttpServletRequest request) {
        try {
            // 读取回调数据
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String xmlData = sb.toString();
            
            // 处理支付回调
            return wechatPayService.handlePayNotify(xmlData);
            
        } catch (Exception e) {
            log.error("处理支付回调失败", e);
            return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[处理失败]]></return_msg></xml>";
        }
    }
    
    /**
     * 查询订单支付状态
     * @param orderNo 订单号
     * @return 支付状态
     */
    @GetMapping("/status/{orderNo}")
    public Result<Boolean> queryPayStatus(@PathVariable String orderNo) {
        log.info("查询订单支付状态: orderNo={}", orderNo);
        return wechatPayService.queryOrderStatus(orderNo);
    }
    
    /**
     * 模拟支付成功（用于测试）
     * @param orderNo 订单号
     * @return 结果
     */
    @PostMapping("/mock/success")
    public Result<Boolean> mockPaySuccess(@RequestParam String orderNo) {
        log.info("模拟支付成功: orderNo={}", orderNo);
        
        ServiceOrder order = orderService.lambdaQuery()
                .eq(ServiceOrder::getOrderNo, orderNo)
                .one();
        
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        // 模拟支付成功
        order.setPayStatus(1);
        order.setOrderStatus(1);
        order.setPayTime(java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        boolean success = orderService.updateById(order);
        return success ? Result.success(true) : Result.error("更新失败");
    }
}
