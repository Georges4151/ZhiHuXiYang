package com.elderly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.entity.EmergencyCall;

public interface EmergencyCallService extends IService<EmergencyCall> {
    
    EmergencyCall createEmergencyCall(EmergencyCall call);
    
    boolean updateCallStatus(Long callId, Integer status, Long responderId);
}
