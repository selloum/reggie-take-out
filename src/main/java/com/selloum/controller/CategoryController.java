package com.selloum.controller;


import ch.qos.logback.classic.Logger;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.selloum.common.Result;
import com.selloum.entity.Category;
import com.selloum.entity.Dish;
import com.selloum.entity.Employee;
import com.selloum.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    @PostMapping
    public Result<String> save(@RequestBody Category category){
        log.info("category:{}",category);
        categoryService.save(category);
        return Result.success("新增分类成功");
    }

    @GetMapping("/page")
    public Result<Page> page(int page,int pageSize){

        Page pageInfo=new Page(page,pageSize);

        LambdaQueryWrapper<Category> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByAsc(Category::getSort);

        categoryService.page(pageInfo,lambdaQueryWrapper);
        return Result.success(pageInfo);
    }

    @DeleteMapping
    public Result<String> delete(Long ids){
        log.info("删除分类id位：{}",ids);

        categoryService.remove(ids);
        return Result.success("删除分类成功");
    }

    @PutMapping
    public Result<String> update(@RequestBody Category category){
        log.info("修改分类信息");


        categoryService.updateById(category);
        return  Result.success("修改分类信息成功");
    }

    /***
     * 根据条件查询菜品分类数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    public Result<List<Category>> list(Category category){

        LambdaQueryWrapper<Category> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        lambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(lambdaQueryWrapper);
        return Result.success(list);
    }


}
