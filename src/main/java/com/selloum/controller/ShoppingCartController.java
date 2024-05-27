package com.selloum.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.selloum.common.BaseContext;
import com.selloum.common.Result;
import com.selloum.entity.ShoppingCart;
import com.selloum.mapper.ShoppingCartMapper;
import com.selloum.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /***
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public Result<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info("购物车数据：{}",shoppingCart);
        log.info("用户和id:{}",BaseContext.getCurrentId());
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        //如果当前插入的菜品或套餐（相同口味）已经在购物车，则要判单一下
        //首先判断是菜品还是套餐
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,currentId);

        if(dishId != null){
            lambdaQueryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else{
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

  //      lambdaQueryWrapper.eq(ShoppingCart::getDishFlavor, shoppingCart.getDishFlavor());
        ShoppingCart one = shoppingCartService.getOne(lambdaQueryWrapper);
        if(one != null){
            //如果已经存在，原来数上+1
            Integer number = one.getNumber();
            one.setNumber(number+1);
            shoppingCartService.updateById(one);

        }else{
            //不存在，添加数据库
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            one=shoppingCart;

        }
        return Result.success(one);
    }

    /***
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    public Result<List<ShoppingCart>> list(){
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        lambdaQueryWrapper.orderByDesc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(lambdaQueryWrapper);

        return Result.success(list);
    }

    @PostMapping("/sub")
    public Result<ShoppingCart> remove(@RequestBody ShoppingCart shoppingCart){
        Long currentUserId = BaseContext.getCurrentId();
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,currentUserId);
        if(dishId!=null){
            lambdaQueryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else{
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        ShoppingCart cartServiceOne = shoppingCartService.getOne(lambdaQueryWrapper);
        Integer number = cartServiceOne.getNumber();
        cartServiceOne.setNumber(number-1);

        shoppingCartService.updateById(cartServiceOne);
        return Result.success(cartServiceOne);
    }

    @DeleteMapping("/clean")
    public Result<String> clean(){
        Long currentUserId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,currentUserId);
        shoppingCartService.remove(lambdaQueryWrapper);

        return Result.success("清空购物车");

    }

}
