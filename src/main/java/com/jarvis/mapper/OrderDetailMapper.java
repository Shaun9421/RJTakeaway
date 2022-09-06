package com.jarvis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jarvis.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}
