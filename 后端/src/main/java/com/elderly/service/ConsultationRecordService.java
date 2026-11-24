package com.elderly.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.entity.ConsultationRecord;

import java.util.List;

public interface ConsultationRecordService extends IService<ConsultationRecord> {
    
    List<ConsultationRecord> getHistoryByUserId(Long userId, Integer limit);
    
    Page<ConsultationRecord> getPageByUserId(Long userId, Integer pageNum, Integer pageSize);
    
    String chatWithDeepSeek(Long userId, String question);
}
