package com.elderly.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.elderly.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("health_data")
public class HealthData extends BaseEntity {
    
    private Long userId;
    
    private BigDecimal bloodPressureHigh;
    
    private BigDecimal bloodPressureLow;
    
    private BigDecimal heartRate;
    
    private BigDecimal bloodSugar;
    
    private BigDecimal temperature;
    
    private BigDecimal weight;
    
    private Integer dataSource;
    
    private String deviceId;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime measureTime;
    
    private String remark;
}
