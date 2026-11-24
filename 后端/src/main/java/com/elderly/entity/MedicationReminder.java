package com.elderly.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.elderly.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("medication_reminder")
public class MedicationReminder extends BaseEntity {
    
    private Long userId;
    
    private String medicationName;
    
    private String dosage;
    
    private String frequency;
    
    private String reminderTime;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private Integer status;
    
    private String remark;
}
