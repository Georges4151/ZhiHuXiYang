package com.elderly.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.elderly.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("consultation_record")
public class ConsultationRecord extends BaseEntity {
    
    private Long userId;
    
    private String question;
    
    private String answer;
    
    private Integer consultationType;
    
    private String voiceUrl;
    
    private String sessionId;
    
    private Integer feedback;
    
    private String feedbackContent;
}
