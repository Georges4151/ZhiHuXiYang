package com.elderly.controller;

import com.elderly.common.Result;
import com.elderly.entity.MedicationReminder;
import com.elderly.service.MedicationReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medication")
public class MedicationReminderController {
    
    @Autowired
    private MedicationReminderService medicationReminderService;
    
    @GetMapping("/list/{userId}")
    public Result<List<MedicationReminder>> getList(@PathVariable Long userId) {
        List<MedicationReminder> list = medicationReminderService.getByUserId(userId);
        return Result.success(list);
    }
    
    @GetMapping("/active/{userId}")
    public Result<List<MedicationReminder>> getActiveList(@PathVariable Long userId) {
        List<MedicationReminder> list = medicationReminderService.getActiveByUserId(userId);
        return Result.success(list);
    }
    
    @GetMapping("/detail/{id}")
    public Result<MedicationReminder> getDetail(@PathVariable Long id) {
        MedicationReminder reminder = medicationReminderService.getById(id);
        return Result.success(reminder);
    }
    
    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody MedicationReminder reminder) {
        boolean success = medicationReminderService.addReminder(reminder);
        return Result.success(success);
    }
    
    @PostMapping("/update")
    public Result<Boolean> update(@RequestBody MedicationReminder reminder) {
        boolean success = medicationReminderService.updateReminder(reminder);
        return Result.success(success);
    }
    
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean success = medicationReminderService.deleteReminder(id);
        return Result.success(success);
    }
    
    @PutMapping("/toggle/{id}/{status}")
    public Result<Boolean> toggleStatus(@PathVariable Long id, @PathVariable Integer status) {
        boolean success = medicationReminderService.toggleStatus(id, status);
        return Result.success(success);
    }
}
