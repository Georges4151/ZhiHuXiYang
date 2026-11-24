package com.elderly.service;

import com.elderly.common.Result;

import java.util.Map;

public interface WechatPayService {
    
    /**
     * 创建微信支付订单
     * @param orderNo 订单号
     * @param amount 金额（元）
     * @param openId 用户openid
     * @param description 商品描述
     * @return 支付参数
     */
    Result<Map<String, String>> createPayOrder(String orderNo, Double amount, String openId, String description);
    
    /**
     * 处理支付回调
     * @param xmlData 回调数据
     * @return 处理结果
     */
    String handlePayNotify(String xmlData);
    
    /**
     * 查询订单支付状态
     * @param orderNo 订单号
     * @return 支付状态
     */
    Result<Boolean> queryOrderStatus(String orderNo);
}
