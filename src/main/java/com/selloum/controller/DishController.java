package com.selloum.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.selloum.common.Result;
import com.selloum.dto.DishDto;
import com.selloum.entity.Category;
import com.selloum.entity.Dish;
import com.selloum.entity.DishFlavor;
import com.selloum.service.CategoryService;
import com.selloum.service.DishFlavorService;
import com.selloum.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    /***
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public Result<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return Result.success("新增菜品成功");
    }

    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, String name){
        //DishDto中的字段包含Dish中字段，所以可用对象拷贝
        Page<Dish> pageInfo=new Page<>(page,pageSize);
        Page<DishDto> pageDtoInfo=new Page<>();

        LambdaQueryWrapper<Dish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(name!=null,Dish::getName,name);
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);

        dishService.page(pageInfo,lambdaQueryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(pageInfo,pageDtoInfo,"records");
        List<Dish> records=pageInfo.getRecords();

        List<DishDto> list=records.stream().map((item)->{
            DishDto dishDto=new DishDto();
            //将除了分类名称的字段（即dish）一整个复制到dishDto
            BeanUtils.copyProperties(item,dishDto);

            //根据dish的分类id查询分类表获取分类名称放入dishDto
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                String name1 = category.getName();
                dishDto.setCategoryName(name1);
            }

            return dishDto;
            //最终将dto对象收集起来,转成集合赋值给
        }).collect(Collectors.toList());

        pageDtoInfo.setRecords(list);

        return Result.success(pageDtoInfo);
    }

    /***
     * 根据id查询菜品信息及口味
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<DishDto> get(@PathVariable Long id){

        DishDto byIdWithFlavor = dishService.getByIdWithFlavor(id);

        return Result.success(byIdWithFlavor);
    }

    @PutMapping
    public Result<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);
        return Result.success("修改菜品成功");
    }

    /***
     * 根据条件查询菜品数据
     * @param dish
     * @return
     */
    //实际上需要传入catagoryId，但是Dish中不仅有catagoryId,还有其他字段，复用性高
    @GetMapping("/list")
    public Result<List<DishDto>> list(Dish dish){

        //----------------------根据分类查询dish菜品--------------------/
        LambdaQueryWrapper<Dish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Dish::getStatus,1);
        lambdaQueryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        lambdaQueryWrapper.orderByDesc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(lambdaQueryWrapper);

        //-----------------------将dish复制到dishDto，并设置dishDto的catagoryName-

        List<DishDto> dishDtoList= list.stream().map((item)->{
            DishDto dishDto=new DishDto();

            BeanUtils.copyProperties(item,dishDto);
            //-----------即根据dish的catagoryId查询catagory表中的catagoryName
            Long categoryId = item.getCategoryId();

            Category category = categoryService.getById(categoryId);
            if(category!=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            //要将flavor传入，所以使用dishDto
            //------从dish中获取dishId，然后根据dishID从dishFlavor表中查对应的口味列表
            Long dishId=item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper1=new LambdaQueryWrapper<>();
            lambdaQueryWrapper1.eq(DishFlavor::getDishId, dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper1);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        return Result.success(dishDtoList);
    }
}
