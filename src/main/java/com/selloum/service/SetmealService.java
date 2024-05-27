package com.selloum.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.selloum.dto.SetmealDto;
import com.selloum.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    /***
     * 新增套餐，同时保存套餐和菜品的关联关系
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    /***
     *
     * 删除套餐和与套餐关联的菜品
     * @param ids
     */
    public void removeWithDish(List<Long> ids);
}
