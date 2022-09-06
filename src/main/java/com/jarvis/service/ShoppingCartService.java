package com.jarvis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jarvis.common.R;
import com.jarvis.entity.ShoppingCart;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

public interface ShoppingCartService extends IService<ShoppingCart> {

    //清空购物车
    R<String> shoopingCartclean();

    //再来一单


    R<String> againSubmit(@RequestBody Map<String, String> map);


    /**
     * 后台管理订单状态
     * @param map
     * @return
     */
    R<String> orderStatusChange(Map<String, String> map);
}
