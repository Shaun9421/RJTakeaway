package com.jarvis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jarvis.entity.Category;

public interface CategoryService extends IService<Category> {

    /**
     * 根据ids删除分类，删除前进行判断
     * @param ids
     */
    public void remove(Long ids);
}
