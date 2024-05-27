package com.selloum.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.selloum.common.CustomException;
import com.selloum.entity.Category;
import com.selloum.entity.Dish;
import com.selloum.entity.Setmeal;
import com.selloum.mapper.CategoryMapper;
import com.selloum.service.CategoryService;
import com.selloum.service.DishService;
import com.selloum.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Override
    public void remove(Long id){

        LambdaQueryWrapper<Dish> lambdaQueryWrapper1=new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.eq(Dish::getCategoryId,id);
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper2=new LambdaQueryWrapper<>();
        lambdaQueryWrapper2.eq(Setmeal::getCategoryId,id);
        long count1 = dishService.count(lambdaQueryWrapper1);
        long count2 = setmealService.count(lambdaQueryWrapper2);

        //查询当前分类是否关联了菜品，若关联，抛异常
        if(count1>0){
            //已经关联菜品
            throw new CustomException("当前分类已关联菜品，无法删除");
        }
        //查询当前分类是否关联了套餐，若关联，抛异常
        if(count2>0){
            //已经关联套餐
            throw new CustomException("当前分类已关联套餐，无法删除");
        }
        super.removeById(id);
    }


}
