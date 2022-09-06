package com.jarvis.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jarvis.common.R;
import com.jarvis.entity.Orders;

import java.util.Map;

public interface OrderService extends IService<Orders> {




    /**
     * 用户下单
     * @param orders
     */
    public void submit(Orders orders);



    /**
     * 用户查询自己的订单
     * @param page
     * @param pageSize
     * @return
     */
    R<Page> userPage(int page, int pageSize);




}