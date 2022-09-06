package com.jarvis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jarvis.common.CustomException;
import com.jarvis.entity.Category;
import com.jarvis.entity.Dish;
import com.jarvis.entity.Setmeal;
import com.jarvis.mapper.CategoryMapper;
import com.jarvis.service.CategoryService;
import com.jarvis.service.DishService;
import com.jarvis.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;
    /**
     * 根据id删除 分类，删除之前需要进行判断是否有关联数据
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        //注意:这里使用count方法的时候一定要传入条件查询的对象，否则计数会出现问题，计算出来的是全部的数据的条数
        int count = dishService.count(dishLambdaQueryWrapper);

        //查询当前分类是否关联了菜品，如果已经管理，直接抛出一个业务异常
        if (count > 0){
            //已经关联了菜品，抛出一个业务异常
            throw new CustomException("当前分类项关联了菜品,不能删除");
        }

        //查询当前分类是否关联了套餐，如果已经管理，直接抛出一个业务异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        //注意:这里使用count方法的时候一定要传入条件查询的对象，否则计数会出现问题，计算出来的是全部的数据的条数
        int setmealCount = setmealService.count(setmealLambdaQueryWrapper);
        if (setmealCount > 0){
            //已经关联了套餐，抛出一个业务异常
            throw new CustomException("当前分类项关联了套餐,不能删除");
        }
        //正常删除
        super.removeById(id);

    }
}
