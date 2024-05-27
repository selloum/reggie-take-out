package com.selloum.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.selloum.entity.User;
import com.selloum.mapper.UserMapper;
import com.selloum.service.UserService;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
