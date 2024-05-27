package com.selloum.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.selloum.entity.Employee;
import com.selloum.mapper.EmployeeMapper;
import com.selloum.service.EmployeeService;
import org.springframework.stereotype.Service;


@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService{



}
