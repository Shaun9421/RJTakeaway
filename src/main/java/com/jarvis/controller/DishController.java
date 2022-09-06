package com.jarvis.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jarvis.common.R;
import com.jarvis.dto.DishDto;
import com.jarvis.entity.Category;
import com.jarvis.entity.Dish;
import com.jarvis.entity.DishFlavor;
import com.jarvis.service.CategoryService;
import com.jarvis.service.DishFlavorService;
import com.jarvis.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;


    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
    dishService.saveWithFlavor(dishDto);

       return R.success("新增菜品成功");
    }

    /**
     * 菜品信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){

        //构造一个分页构造器对象
        Page<Dish> dishPage = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>(page,pageSize);
        //上面对dish泛型的数据已经赋值了，这里对DishDto我们可以把之前的数据拷贝过来进行赋值

        //构造一个条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件 注意判断是否为空  使用对name的模糊查询
        queryWrapper.like(name != null,Dish::getName,name);
        //添加排序条件  根据更新时间降序排
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //去数据库处理分页 和 查询
        dishService.page(dishPage,queryWrapper);

        //获取到dish的所有数据 records属性是分页插件中表示分页中所有的数据的一个集合
        List<Dish> records = dishPage.getRecords();

        List<DishDto> list = records.stream().map((item) ->{
            //对实体类DishDto进行categoryName的设值

            DishDto dishDto = new DishDto();
            //这里的item相当于Dish  对dishDto进行除categoryName属性的拷贝
            BeanUtils.copyProperties(item,dishDto);
            //获取分类的id
            Long categoryId = item.getCategoryId();
            //通过分类id获取分类对象
            Category category = categoryService.getById(categoryId);
            if ( category != null){
                //设置实体类DishDto的categoryName属性值
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        //对象拷贝  使用框架自带的工具类，第三个参数是不拷贝到属性
        BeanUtils.copyProperties(dishPage,dishDtoPage,"records");
        dishDtoPage.setRecords(list);
        //因为上面处理的数据没有分类的id,这样直接返回R.success(dishPage)虽然不会报错，但是前端展示的时候这个菜品分类这一数据就为空
        //所以进行了上面的一系列操作
        return R.success(dishDtoPage);
    }


    /**
     * 根据id 查询菜品信息所对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) { //@PathVariable 路径变量
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return  R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());

        dishService.updateWithFlavor(dishDto);

        return R.success("修改菜品成功");
    }

    /**
     * 菜品的停售与起售   批量停售与单个菜品停售
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable("status") Integer status,@RequestParam List<Long> ids){
        log.info("status:{}",status);
        log.info("ids:{}",ids);
        //获取到该商品的ids
        //查找所选ids
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids != null,Dish::getId,ids);
        List<Dish> list = dishService.list(queryWrapper);
        //单个查询停售与起售
        /*Dish  dish = dishService.getById(ids);
        if (dish != null ) {
            dish.setStatus(status);
            dishService.updateById(dish);
            return R.success("开始起售");
        }
        return R.error("售卖状态设置失败");*/
        for (Dish dish :list){
            if (dish != null) {
                dish.setStatus(status);
                dishService.updateById(dish);
            }

        }


        return R.success("售卖状态修改成功");
    }

    //
    @DeleteMapping
    private R<String> delete(@RequestParam("ids") List<Long> ids) {
        //删除菜品
        dishService.deleteById(ids);
        //删除对应的口味
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(queryWrapper);
        return R.success("菜品删除成功");
    }

    /**
     * 根据条件查询对应的菜品数据   添加套餐的菜品选择
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        //构造查询条件对象
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //获取到菜品id
        queryWrapper.eq(dish.getCategoryId() != null ,Dish::getCategoryId,dish.getCategoryId());
        //只查询在售的菜品
        queryWrapper.eq(Dish::getStatus,1);
        //排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);



        List<DishDto> dishDtoList = list.stream().map((item) ->{
            //对实体类DishDto进行categoryName的设值

            DishDto dishDto = new DishDto();
            //这里的item相当于Dish  对dishDto进行除categoryName属性的拷贝
            BeanUtils.copyProperties(item,dishDto);
            //获取分类的id
            Long categoryId = item.getCategoryId();
            //通过分类id获取分类对象
            Category category = categoryService.getById(categoryId);
            if ( category != null){
                //设置实体类DishDto的categoryName属性值
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            //查询口味前台页面展示  查询当前菜品id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());


        return R.success(dishDtoList);
    }



}
