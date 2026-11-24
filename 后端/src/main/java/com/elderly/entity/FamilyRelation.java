package com.elderly.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.elderly.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("family_relation")
public class FamilyRelation extends BaseEntity {
    
    private Long userId;
    
    private Long familyUserId;
    
    private String name;
    
    private String phone;
    
    private String relation;
    
    private Integer isPrimary;
}
