package com.selloum.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.selloum.entity.OrderDetail;
import com.selloum.mapper.OrderDetailMapper;
import com.selloum.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
