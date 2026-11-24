package com.elderly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.entity.ServiceItem;

import java.util.List;

public interface ServiceItemService extends IService<ServiceItem> {
    
    List<ServiceItem> getByCategoryId(Integer categoryId);
    
    List<ServiceItem> getHotServices(Integer limit);
}
