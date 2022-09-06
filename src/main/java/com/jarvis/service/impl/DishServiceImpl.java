package com.jarvis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jarvis.common.CustomException;
import com.jarvis.dto.DishDto;
import com.jarvis.entity.Dish;
import com.jarvis.entity.DishFlavor;
import com.jarvis.mapper.DishFlavorMapper;
import com.jarvis.mapper.DishMapper;
import com.jarvis.service.DishFlavorService;
import com.jarvis.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;
    //新增菜品同时保存对应的口味数据
    @Transactional //开启事务
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息
         this.save(dishDto);
        //取到菜品id
        Long dishId = dishDto.getId();

        //保存菜品的口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        //将dishid一起封装
        flavors = flavors.stream().map(item -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(dishDto.getFlavors());
    }

    /**
     * 根据菜品id查询菜品信息和口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //从dish 表中查询基本信息
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();

        BeanUtils.copyProperties(dish,dishDto);

        //从dish_flavor查询当前菜品对应的口味信息
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(lambdaQueryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    /**
     * 更新菜品信息同时更新对应的口味信息
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表的基本信息  因为这里的dishDto是dish的子类
        this.updateById(dishDto);

        //更新口味信息---》先清理再重新插入口味信息
        //清理当前菜品对应口味数据---dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        //添加当前提交过来的口味数据---dish_flavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();

        //下面这段流的代码我注释,然后测试，发现一次是报dishId没有默认值(先测)，两次可以得到结果(后测，重新编译过，清除缓存过),相隔半个小时
        //因为这里拿到的flavorsz只有name和value(这是在设计数据封装的问题),不过debug测试的时候发现有时候可以拿到全部数据,有时候又不可以...  所以还是加上吧。。。。。
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);

    }

    /**
     * 菜品的批量删除和单个删除  以事物的方式提交
     * @param ids
     */
    @Override
    @Transactional
    public void deleteById(List<Long> ids) {
        //构造条件查询器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //查询菜品是否在售卖
        queryWrapper.in(ids != null,Dish::getId,ids);
        //封装成一个list 对象
        List<Dish> list = this.list(queryWrapper);

        for ( Dish dish : list) {
           Integer status = dish.getStatus();
           if (status == 0) {
               //如果不在售卖，则可以删除
               this.removeById(dish.getId());
           }else {
               //需要回滚，前面可能删除了，后面还在售卖
               throw new CustomException("删除的菜品中有正在售卖的菜品，无法删除");
           }
        }
    }
}
