package com.elderly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.entity.FamilyRelation;

import java.util.List;

public interface FamilyRelationService extends IService<FamilyRelation> {
    
    List<FamilyRelation> getByUserId(Long userId);
    
    FamilyRelation addFamily(FamilyRelation relation);
    
    boolean updateFamily(FamilyRelation relation);
    
    boolean deleteFamily(Long id);
    
    boolean setPrimary(Long userId, Long familyId);
}
