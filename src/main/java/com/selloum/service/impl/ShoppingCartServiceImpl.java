package com.selloum.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.selloum.entity.ShoppingCart;
import com.selloum.mapper.ShoppingCartMapper;
import com.selloum.service.ShoppingCartService;
import org.springframework.stereotype.Service;


@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
