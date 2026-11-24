package com.elderly.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.elderly.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("emergency_call")
public class EmergencyCall extends BaseEntity {
    
    private Long userId;
    
    private String userName;
    
    private String userPhone;
    
    private Double latitude;
    
    private Double longitude;
    
    private String address;
    
    private Integer callType;
    
    private Integer callStatus;
    
    private LocalDateTime responseTime;
    
    private Long responderId;
    
    private String responderName;
    
    private String responderPhone;
    
    private String remark;
}
