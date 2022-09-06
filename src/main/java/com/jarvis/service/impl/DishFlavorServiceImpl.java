package com.jarvis.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jarvis.entity.DishFlavor;
import com.jarvis.mapper.DishFlavorMapper;
import com.jarvis.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {

}
