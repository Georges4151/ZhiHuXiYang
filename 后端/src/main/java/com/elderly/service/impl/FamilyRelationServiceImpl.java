package com.elderly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.entity.FamilyRelation;
import com.elderly.mapper.FamilyRelationMapper;
import com.elderly.service.FamilyRelationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FamilyRelationServiceImpl extends ServiceImpl<FamilyRelationMapper, FamilyRelation> implements FamilyRelationService {
    
    @Override
    public List<FamilyRelation> getByUserId(Long userId) {
        LambdaQueryWrapper<FamilyRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FamilyRelation::getUserId, userId)
               .orderByDesc(FamilyRelation::getIsPrimary);
        return list(wrapper);
    }
    
    @Override
    public FamilyRelation addFamily(FamilyRelation relation) {
        save(relation);
        return relation;
    }
    
    @Override
    public boolean updateFamily(FamilyRelation relation) {
        return updateById(relation);
    }
    
    @Override
    public boolean deleteFamily(Long id) {
        return removeById(id);
    }
    
    @Override
    public boolean setPrimary(Long userId, Long familyId) {
        LambdaUpdateWrapper<FamilyRelation> clearWrapper = new LambdaUpdateWrapper<>();
        clearWrapper.eq(FamilyRelation::getUserId, userId)
                    .set(FamilyRelation::getIsPrimary, 0);
        update(clearWrapper);
        
        LambdaUpdateWrapper<FamilyRelation> setWrapper = new LambdaUpdateWrapper<>();
        setWrapper.eq(FamilyRelation::getId, familyId)
                  .set(FamilyRelation::getIsPrimary, 1);
        return update(setWrapper);
    }
}
