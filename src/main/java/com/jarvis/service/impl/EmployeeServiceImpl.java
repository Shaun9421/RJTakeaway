package com.jarvis.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jarvis.entity.Employee;
import com.jarvis.mapper.EmployeeMapper;
import com.jarvis.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
