package com.elderly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.entity.MedicationReminder;

import java.util.List;

public interface MedicationReminderService extends IService<MedicationReminder> {
    
    List<MedicationReminder> getByUserId(Long userId);
    
    List<MedicationReminder> getActiveByUserId(Long userId);
    
    boolean addReminder(MedicationReminder reminder);
    
    boolean updateReminder(MedicationReminder reminder);
    
    boolean deleteReminder(Long id);
    
    boolean toggleStatus(Long id, Integer status);
}
