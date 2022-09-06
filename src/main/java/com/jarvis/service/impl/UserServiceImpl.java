package com.jarvis.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jarvis.entity.User;
import com.jarvis.mapper.UserMapper;
import com.jarvis.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
