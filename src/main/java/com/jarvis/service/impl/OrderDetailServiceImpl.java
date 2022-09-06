package com.jarvis.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jarvis.entity.OrderDetail;
import com.jarvis.mapper.OrderDetailMapper;
import com.jarvis.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
