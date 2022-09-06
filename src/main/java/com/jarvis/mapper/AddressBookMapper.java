package com.jarvis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jarvis.entity.AddressBook;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
