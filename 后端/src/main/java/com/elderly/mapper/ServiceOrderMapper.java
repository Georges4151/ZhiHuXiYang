package com.elderly.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.elderly.entity.ServiceOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ServiceOrderMapper extends BaseMapper<ServiceOrder> {
    
    @Select("SELECT * FROM service_order WHERE order_no = #{orderNo} AND deleted = 0")
    ServiceOrder selectByOrderNo(String orderNo);
}
