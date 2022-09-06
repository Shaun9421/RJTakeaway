package com.jarvis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jarvis.common.CustomException;
import com.jarvis.common.R;
import com.jarvis.dto.SetmealDto;
import com.jarvis.entity.Setmeal;
import com.jarvis.entity.SetmealDish;
import com.jarvis.mapper.SetmealMapper;
import com.jarvis.service.SetmealDishService;
import com.jarvis.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    SetmealDishService setmealDishService;

    /**
     * 修改套餐的回显
     * @param id
     * @return
     */
    @Override
    public SetmealDto getDate(Long id) {
        //获取到套餐id
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        //在关联表中查询setmealDish
        queryWrapper.eq(id != null, SetmealDish::getSetmealId,id);

        if (setmeal != null) {
            BeanUtils.copyProperties(setmeal, setmealDto);
            List<SetmealDish> setmealDishList = setmealDishService.list(queryWrapper);
            setmealDto.setSetmealDishes(setmealDishList);
            return setmealDto;
        }

        return null;
    }

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息，操作setmeal,执行insert
        this.save(setmealDto);
        log.info(setmealDto.toString()); //查看一下这个套餐的基本信息是什么

        //保存套餐和菜品的关联信息，操作setmeal_dish ,执行insert操作
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //注意上面拿到的setmealDishes是没有setmeanlId这个的值的，通过debug可以发现
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item; //这里返回的就是集合的泛型
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes); //批量保存
    }

    /**
     * 删除套餐。同时删除与套餐关联的信息
     * @param ids
     */
    @Override
    @Transactional //操作两张表，保证事务的一致性
    public void removeWithDish(List<Long> ids) {
        //状态是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids != null, Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        int count = this.count(queryWrapper);
        //不能删除抛出业务异常
        if (count > 0) {
            throw new CustomException("套餐正在售卖中，不能删除");
        }


        //可以删除，先删除套餐表中的数据
        this.removeByIds(ids);

        //删除关系表中的数据
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(ids != null,SetmealDish::getSetmealId,ids);

        setmealDishService.remove(lambdaQueryWrapper);

    }

    /**
     * 根据套餐id修改售卖状态
     * @param status
     * @param ids
     */
    @Override
    public void updateSetmealStatusById(Integer status,  List<Long> ids) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.in(ids !=null,Setmeal::getId,ids);
        List<Setmeal> list = this.list(queryWrapper);

        for (Setmeal setmeal : list) {
            if (setmeal != null){
                setmeal.setStatus(status);
                this.updateById(setmeal);
            }
        }
    }

}