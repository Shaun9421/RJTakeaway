package com.jarvis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jarvis.common.BaseContext;
import com.jarvis.common.R;
import com.jarvis.entity.AddressBook;
import com.jarvis.mapper.AddressBookMapper;
import com.jarvis.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

    @Autowired
    private AddressBookService addressBookService;
    /**
     * 删除地址
     * @param id
     * @return
     */
    @Override
    public R<String> deleteById(Long id) {
        if (id == null){
            return R.error("请求异常");
        }
        LambdaQueryWrapper<AddressBook> query = new LambdaQueryWrapper<>();
        query.eq(AddressBook::getId, id).eq(AddressBook::getUserId, BaseContext.getCurrentId());
        addressBookService.remove(query);

        return R.success("地址删除成功");
    }
}
