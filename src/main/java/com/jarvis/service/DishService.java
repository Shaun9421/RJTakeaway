package com.jarvis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jarvis.dto.DishDto;
import com.jarvis.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    //新增菜品操作两张表
    //同时插入菜品的口味数据
    public void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品id和口味信息
    public DishDto getByIdWithFlavor(Long id);

    //更新菜品信息同时更新对应口味信息
    public void updateWithFlavor(DishDto dishDto);

    void deleteById(List<Long> ids);
}
