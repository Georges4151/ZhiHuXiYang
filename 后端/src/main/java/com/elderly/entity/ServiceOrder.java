package com.elderly.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.elderly.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("service_order")
public class ServiceOrder extends BaseEntity {
    
    private String orderNo;
    
    private Long userId;
    
    @TableField("service_item_id")
    private Long serviceId;
    
    private String serviceName;
    
    @TableField("price")
    private BigDecimal amount;
    
    @TableField("status")
    private Integer orderStatus;
    
    @TableField("address")
    private String serviceAddress;
    
    private String contactName;
    
    private String contactPhone;
    
    @TableField("service_time")
    private String appointmentTime;
    
    private Long workerId;
    
    private String workerName;
    
    private String workerPhone;
    
    private Integer serviceRating;
    
    private String serviceComment;
    
    private Integer payStatus;
    
    private String payTime;
}
