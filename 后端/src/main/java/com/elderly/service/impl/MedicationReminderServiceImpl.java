package com.elderly.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.entity.MedicationReminder;
import com.elderly.mapper.MedicationReminderMapper;
import com.elderly.service.MedicationReminderService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MedicationReminderServiceImpl extends ServiceImpl<MedicationReminderMapper, MedicationReminder> implements MedicationReminderService {
    
    @Override
    public List<MedicationReminder> getByUserId(Long userId) {
        return lambdaQuery()
                .eq(MedicationReminder::getUserId, userId)
                .orderByDesc(MedicationReminder::getCreateTime)
                .list();
    }
    
    @Override
    public List<MedicationReminder> getActiveByUserId(Long userId) {
        LocalDate today = LocalDate.now();
        return lambdaQuery()
                .eq(MedicationReminder::getUserId, userId)
                .eq(MedicationReminder::getStatus, 1)
                .le(MedicationReminder::getStartDate, today)
                .ge(MedicationReminder::getEndDate, today)
                .orderByAsc(MedicationReminder::getReminderTime)
                .list();
    }
    
    @Override
    public boolean addReminder(MedicationReminder reminder) {
        // 参数校验
        if (reminder == null) {
            return false;
        }
        if (reminder.getUserId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (reminder.getMedicationName() == null || reminder.getMedicationName().trim().isEmpty()) {
            throw new IllegalArgumentException("药品名称不能为空");
        }
        // 设置默认值
        if (reminder.getStatus() == null) {
            reminder.setStatus(1);
        }
        return save(reminder);
    }
    
    @Override
    public boolean updateReminder(MedicationReminder reminder) {
        // 参数校验
        if (reminder == null || reminder.getId() == null) {
            return false;
        }
        if (reminder.getMedicationName() != null && reminder.getMedicationName().trim().isEmpty()) {
            throw new IllegalArgumentException("药品名称不能为空");
        }
        return updateById(reminder);
    }
    
    @Override
    public boolean deleteReminder(Long id) {
        return removeById(id);
    }
    
    @Override
    public boolean toggleStatus(Long id, Integer status) {
        MedicationReminder reminder = getById(id);
        if (reminder != null) {
            reminder.setStatus(status);
            return updateById(reminder);
        }
        return false;
    }
}
