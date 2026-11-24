package com.elderly.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.entity.EmergencyCall;
import com.elderly.mapper.EmergencyCallMapper;
import com.elderly.service.EmergencyCallService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmergencyCallServiceImpl extends ServiceImpl<EmergencyCallMapper, EmergencyCall> implements EmergencyCallService {
    
    @Override
    public EmergencyCall createEmergencyCall(EmergencyCall call) {
        call.setCallStatus(0);
        call.setCallType(1);
        this.save(call);
        return call;
    }
    
    @Override
    public boolean updateCallStatus(Long callId, Integer status, Long responderId) {
        EmergencyCall call = this.getById(callId);
        if (call != null) {
            call.setCallStatus(status);
            call.setResponseTime(LocalDateTime.now());
            return this.updateById(call);
        }
        return false;
    }
}
