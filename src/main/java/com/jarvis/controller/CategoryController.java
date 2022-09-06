package com.jarvis.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jarvis.common.R;
import com.jarvis.entity.Category;
import com.jarvis.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 分类管理
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("{category}" ,category);
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){

        //创建一个分页构造器
        Page<Category> categoryPage = new Page<>(page,pageSize);
        //创建一个条件构造器  用来排序用的  注意这个条件构造器一定要使用泛型，否则使用条件查询这个方法的时候会报错
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();
        //添加排序条件 ，根据sort字段进行排序
        queryWrapper.orderByAsc(Category::getSort);
        //分页查询
        categoryService.page(categoryPage,queryWrapper);
        return R.success(categoryPage);
    }

    /**
     * 根据id来删除分类的数据
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam("ids") Long id){ //注意这里前端传过来的数据是ids

        categoryService.remove(id);
        return R.success("分类信息删除成功");

    }

    /**
     * 根据id信息更改分类数据
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改分类信息");

        categoryService.updateById(category);

        return R.success("修改分类信息成功");
    }


    /**
     * 根据条件查询分类数据  下拉框
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category) { //封装成实体类
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(category.getType() != null, Category::getType,category.getType());
        //添加排序分类   sort相同使用更新时间降序排序
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(queryWrapper);
        log.info("");
        return R.success(list);

    }

}
