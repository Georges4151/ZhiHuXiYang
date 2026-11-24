package com.elderly.service.impl;

import com.elderly.common.Result;
import com.elderly.config.WechatPayConfig;
import com.elderly.entity.ServiceOrder;
import com.elderly.mapper.ServiceOrderMapper;
import com.elderly.service.WechatPayService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class WechatPayServiceImpl implements WechatPayService {
    
    @Autowired
    private WechatPayConfig wechatPayConfig;
    
    @Autowired
    private ServiceOrderMapper orderMapper;
    
    private static final String UNIFIED_ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    private static final String ORDER_QUERY_URL = "https://api.mch.weixin.qq.com/pay/orderquery";
    
    @Override
    public Result<Map<String, String>> createPayOrder(String orderNo, Double amount, String openId, String description) {
        try {
            // 检查配置
            if (wechatPayConfig.getAppId() == null || wechatPayConfig.getMchId() == null || wechatPayConfig.getApiKey() == null) {
                log.warn("微信支付配置不完整，使用模拟支付");
                // 返回模拟支付数据
                Map<String, String> result = new HashMap<>();
                result.put("appId", "mock_app_id");
                result.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
                result.put("nonceStr", generateNonceStr());
                result.put("package", "prepay_id=mock_" + orderNo);
                result.put("signType", "MD5");
                result.put("paySign", "mock_sign");
                return Result.success(result);
            }
            
            // 构建请求参数
            Map<String, String> params = new TreeMap<>();
            params.put("appid", wechatPayConfig.getAppId());
            params.put("mch_id", wechatPayConfig.getMchId());
            params.put("nonce_str", generateNonceStr());
            params.put("body", description);
            params.put("out_trade_no", orderNo);
            params.put("total_fee", String.valueOf((int)(amount * 100))); // 转换为分
            params.put("spbill_create_ip", "127.0.0.1");
            params.put("notify_url", wechatPayConfig.getNotifyUrl());
            params.put("trade_type", "JSAPI");
            params.put("openid", openId);
            
            // 生成签名
            String sign = generateSign(params, wechatPayConfig.getApiKey());
            params.put("sign", sign);
            
            // 转换为XML
            String xmlData = mapToXml(params);
            log.info("统一下单请求: {}", xmlData);
            
            // 发送请求（实际项目中使用RestTemplate或HttpClient）
            // 这里返回模拟数据用于测试
            // RestTemplate restTemplate = new RestTemplate();
            // String response = restTemplate.postForObject(UNIFIED_ORDER_URL, xmlData, String.class);
            
            // 模拟返回数据
            Map<String, String> result = new HashMap<>();
            result.put("appId", wechatPayConfig.getAppId());
            result.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
            result.put("nonceStr", generateNonceStr());
            result.put("package", "prepay_id=wx" + generateNonceStr());
            result.put("signType", "MD5");
            
            // 生成支付签名
            String paySign = generateSign(result, wechatPayConfig.getApiKey());
            result.put("paySign", paySign);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("创建支付订单失败: {}", e.getMessage());
            return Result.error("创建支付订单失败: " + e.getMessage());
        }
    }
    
    @Override
    public String handlePayNotify(String xmlData) {
        try {
            log.info("收到支付回调: {}", xmlData);
            
            // 解析XML
            Map<String, String> result = xmlToMap(xmlData);
            
            // 验证签名
            String sign = result.remove("sign");
            String calculatedSign = generateSign(result, wechatPayConfig.getApiKey());
            
            if (!calculatedSign.equals(sign)) {
                log.error("支付回调签名验证失败");
                return generateXmlResponse("FAIL", "签名验证失败");
            }
            
            // 处理支付结果
            String returnCode = result.get("return_code");
            String resultCode = result.get("result_code");
            String orderNo = result.get("out_trade_no");
            
            if ("SUCCESS".equals(returnCode) && "SUCCESS".equals(resultCode)) {
                // 更新订单状态
                ServiceOrder order = orderMapper.selectByOrderNo(orderNo);
                if (order != null) {
                    order.setPayStatus(1); // 已支付
                    order.setPayTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    order.setOrderStatus(1); // 待服务
                    orderMapper.updateById(order);
                    log.info("订单 {} 支付成功", orderNo);
                }
                return generateXmlResponse("SUCCESS", "OK");
            } else {
                log.error("支付失败: {}", result.get("err_code_des"));
                return generateXmlResponse("FAIL", "支付失败");
            }
            
        } catch (Exception e) {
            log.error("处理支付回调失败: {}", e.getMessage());
            return generateXmlResponse("FAIL", "处理失败");
        }
    }
    
    @Override
    public Result<Boolean> queryOrderStatus(String orderNo) {
        try {
            Map<String, String> params = new TreeMap<>();
            params.put("appid", wechatPayConfig.getAppId());
            params.put("mch_id", wechatPayConfig.getMchId());
            params.put("out_trade_no", orderNo);
            params.put("nonce_str", generateNonceStr());
            
            String sign = generateSign(params, wechatPayConfig.getApiKey());
            params.put("sign", sign);
            
            String xmlData = mapToXml(params);
            log.info("查询订单请求: {}", xmlData);
            
            // 实际项目中发送HTTP请求
            // RestTemplate restTemplate = new RestTemplate();
            // String response = restTemplate.postForObject(ORDER_QUERY_URL, xmlData, String.class);
            
            // 查询本地订单状态
            ServiceOrder order = orderMapper.selectByOrderNo(orderNo);
            if (order != null && order.getPayStatus() == 1) {
                return Result.success(true);
            }
            
            return Result.success(false);
            
        } catch (Exception e) {
            log.error("查询订单状态失败: {}", e.getMessage());
            return Result.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成随机字符串
     */
    private String generateNonceStr() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 32);
    }
    
    /**
     * 生成MD5签名
     */
    private String generateSign(Map<String, String> params, String apiKey) {
        try {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                    sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
                }
            }
            sb.append("key=").append(apiKey);
            
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
            
            StringBuilder sign = new StringBuilder();
            for (byte b : digest) {
                sign.append(String.format("%02x", b));
            }
            return sign.toString().toUpperCase();
            
        } catch (Exception e) {
            throw new RuntimeException("生成签名失败", e);
        }
    }
    
    /**
     * Map转XML
     */
    private String mapToXml(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append("<").append(entry.getKey()).append(">");
            sb.append("<![CDATA[").append(entry.getValue()).append("]]>");
            sb.append("</").append(entry.getKey()).append(">");
        }
        sb.append("</xml>");
        return sb.toString();
    }
    
    /**
     * XML转Map
     */
    private Map<String, String> xmlToMap(String xml) {
        Map<String, String> map = new HashMap<>();
        try {
            javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document doc = builder.parse(new java.io.ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
            org.w3c.dom.NodeList nodeList = doc.getDocumentElement().getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                org.w3c.dom.Node node = nodeList.item(i);
                if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    map.put(node.getNodeName(), node.getTextContent());
                }
            }
        } catch (Exception e) {
            log.error("解析XML失败", e);
        }
        return map;
    }
    
    /**
     * 生成XML响应
     */
    private String generateXmlResponse(String returnCode, String returnMsg) {
        return "<xml>" +
                "<return_code><![CDATA[" + returnCode + "]]></return_code>" +
                "<return_msg><![CDATA[" + returnMsg + "]]></return_msg>" +
                "</xml>";
    }
}
