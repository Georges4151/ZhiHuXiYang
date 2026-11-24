package com.elderly.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "wechat.pay")
public class WechatPayConfig {
    
    private String appId;
    private String mchId;
    private String apiKey;
    private String notifyUrl;
}
