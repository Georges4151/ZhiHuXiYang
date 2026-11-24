package com.elderly.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class HealthDataRequest {
    
    private Long userId;
    
    private BigDecimal bloodPressureHigh;
    
    private BigDecimal bloodPressureLow;
    
    private BigDecimal heartRate;
    
    private BigDecimal bloodSugar;
    
    private BigDecimal temperature;
    
    private BigDecimal weight;
    
    private String measureTime;
    
    private String remark;
}
