package com.selloum.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.selloum.common.Result;
import com.selloum.dto.SetmealDto;
import com.selloum.entity.Category;
import com.selloum.entity.Setmeal;
import com.selloum.service.CategoryService;
import com.selloum.service.SetmealDishService;
import com.selloum.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/***
 *
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping()
    public Result<String> save(@RequestBody SetmealDto setmealDto){

        log.info("套餐信息：{}",setmealDto);

        setmealService.saveWithDish(setmealDto);


        return Result.success("新增套餐成功");
    }

    @GetMapping("/page")
    public Result<Page> page(int page,int pageSize,String name){

        /*//页面的套餐分类不会显示，因为Setmeal中只有categoryId，没有对应的套餐分类名称
        Page<Setmeal> pageInfo=new Page<>();

        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(name!=null,Setmeal::getName,name);
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo, lambdaQueryWrapper);*/

        Page<Setmeal> pageInfo=new Page<>();
        //SetmealDto中有categoryName字段,是最终页面的page
        Page<SetmealDto> dtoPage=new Page<>();



        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(name!=null,Setmeal::getName,name);
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo, lambdaQueryWrapper);

        //records不用拷贝，因为泛型不一样
        //将页面的records由Setmeal修改为SetmealDto类型
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<Setmeal> setmealRecords = pageInfo.getRecords();

        List<SetmealDto> setmealDtoRecords= setmealRecords.stream().map((item)->{
            SetmealDto setmealDto=new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);

            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());


        dtoPage.setRecords(setmealDtoRecords);

        return Result.success(dtoPage);
    }

    /***
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result<String> delete(@RequestParam List<Long> ids){
        log.info("ids:{}",ids);
        setmealService.removeWithDish(ids);
        return Result.success("套餐数据删除成功");
    }

    ///setmeal/list?categoryId=1413386191767674881&status=1格式不使用@RequestBody，仅json使用
    @GetMapping("/list")
    public Result<List<Setmeal>> list(Setmeal setmeal){
        log.info("Setmeal:{}",setmeal);

        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper= new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        lambdaQueryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> setmealList = setmealService.list(lambdaQueryWrapper);

        return Result.success(setmealList);
    }

}
