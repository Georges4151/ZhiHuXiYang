package com.elderly.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.elderly.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("address")
public class Address extends BaseEntity {
    
    private Long userId;
    
    private String name;
    
    private String phone;
    
    private String province;
    
    private String city;
    
    private String district;
    
    private String detail;
    
    private String label;
    
    private Integer isDefault;
}
