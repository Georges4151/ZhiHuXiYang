package com.elderly.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.elderly.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("family_activity")
public class FamilyActivity extends BaseEntity {
    
    private Long userId;
    
    private String type;
    
    private String title;
    
    private String description;
    
    private String extraData;
    
    public static final String TYPE_LOCATION = "location";
    public static final String TYPE_HEALTH = "health";
    public static final String TYPE_ALERT = "alert";
    public static final String TYPE_INVITE = "invite";
}
