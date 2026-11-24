package com.elderly.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.elderly.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("service_item")
public class ServiceItem extends BaseEntity {
    
    private String name;
    
    private String description;
    
    private BigDecimal price;
    
    private String image;
    
    private Integer category;
    
    private Integer duration;
    
    private Integer sales;
    
    private BigDecimal rating;
    
    private String icon;
    
    private Integer status;
    
    private Integer sort;
}
