package com.elderly.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.elderly.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("elderly_user")
public class ElderlyUser extends BaseEntity {
    
    private String openId;
    
    private String name;
    
    private String phone;
    
    private String password;
    
    private String avatar;
    
    private Integer gender;
    
    private LocalDate birthday;
    
    private String idCard;
    
    private String address;
    
    private Double latitude;
    
    private Double longitude;
    
    private String emergencyContact;
    
    private String emergencyPhone;
    
    private Integer userType;
    
    private String healthCondition;
    
    private Integer status;
}
