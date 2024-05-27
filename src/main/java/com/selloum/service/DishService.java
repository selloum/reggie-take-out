package com.selloum.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.selloum.dto.DishDto;
import com.selloum.entity.Dish;

public interface DishService extends IService<Dish> {

    public void saveWithFlavor(DishDto dishDto);

    public DishDto getByIdWithFlavor(Long id);

    public void updateWithFlavor(DishDto dishDto);
}
