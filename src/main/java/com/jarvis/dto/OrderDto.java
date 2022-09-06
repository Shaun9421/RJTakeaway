package com.jarvis.dto;

import com.jarvis.entity.OrderDetail;
import com.jarvis.entity.Orders;
import lombok.Data;

import java.util.List;

@Data
public class OrderDto extends Orders {

    private List<OrderDetail> orderDetail;


    public void setOrderDetails(List<OrderDetail> orderDetailList) {

    }
}
