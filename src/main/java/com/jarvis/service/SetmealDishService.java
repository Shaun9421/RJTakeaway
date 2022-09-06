package com.jarvis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jarvis.common.R;
import com.jarvis.dto.DishDto;
import com.jarvis.entity.SetmealDish;

import java.util.List;


public interface SetmealDishService extends IService<SetmealDish> {


    /**
     * 获取套餐详情
     *
     * @param setmealId
     * @return
     */
    R<List<DishDto>> getDishDto(Long setmealId);
}
