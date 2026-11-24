package com.elderly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.entity.ServiceItem;
import com.elderly.mapper.ServiceItemMapper;
import com.elderly.service.ServiceItemService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceItemServiceImpl extends ServiceImpl<ServiceItemMapper, ServiceItem> implements ServiceItemService {
    
    @Override
    public List<ServiceItem> getByCategoryId(Integer categoryId) {
        return this.list(new LambdaQueryWrapper<ServiceItem>()
                .eq(ServiceItem::getCategory, categoryId)
                .eq(ServiceItem::getStatus, 1)
                .orderByAsc(ServiceItem::getSort));
    }
    
    @Override
    public List<ServiceItem> getHotServices(Integer limit) {
        return this.list(new LambdaQueryWrapper<ServiceItem>()
                .eq(ServiceItem::getStatus, 1)
                .orderByDesc(ServiceItem::getSales)
                .last("LIMIT " + limit));
    }
}
