package com.jarvis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jarvis.entity.Setmeal;
import com.jarvis.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SetmealDishMapper  extends BaseMapper<SetmealDish> {
}
