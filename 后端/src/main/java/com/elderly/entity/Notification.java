package com.elderly.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.elderly.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("notification")
public class Notification extends BaseEntity {
    
    private Long userId;
    
    private String title;
    
    private String content;
    
    private Integer type;
    
    private Integer isRead;
    
    private String extraData;

    public static final int TYPE_SYSTEM = 0;
    public static final int TYPE_SERVICE = 1;
    public static final int TYPE_HEALTH = 2;
    public static final int TYPE_ACTIVITY = 3;
}
