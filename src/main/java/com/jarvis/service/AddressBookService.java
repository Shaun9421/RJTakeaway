package com.jarvis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jarvis.common.R;
import com.jarvis.entity.AddressBook;


public interface AddressBookService extends IService<AddressBook> {

    /**
     * 删除地址
     * @param id
     * @return
     */
    R<String> deleteById(Long id);
}
