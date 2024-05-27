package com.selloum.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.selloum.entity.DishFlavor;
import com.selloum.mapper.DishFlavorMapper;
import com.selloum.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
