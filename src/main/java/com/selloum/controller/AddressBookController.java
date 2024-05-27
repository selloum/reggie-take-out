package com.selloum.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.selloum.common.BaseContext;
import com.selloum.common.Result;
import com.selloum.entity.AddressBook;
import com.selloum.service.AddressBookService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addressBook")
@Slf4j
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    @PostMapping
    public Result<AddressBook> save(@RequestBody AddressBook addressBook, HttpSession session){
        String userID = session.getAttribute("user").toString();
        log.info("userId:{}",userID);
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook:{}",addressBook);
        log.info("addressBookUserID:{}",addressBook.getUserId());
        addressBookService.save(addressBook);
        return Result.success(addressBook);
    }

    /**
     * 根据id查询地址
     */
    @GetMapping("/{id}")
    public Result get(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null) {
            return Result.success(addressBook);
        } else {
            return Result.error("没有找到该对象");
        }
    }

    /***
     *
     * 设置默认地址
     *1-将该用户的地址全部设为非默认user_id
     * 2-将该条记录设为默认 id
     * @param addressBook
     * @return
     */
    @PutMapping("default")
    public Result<AddressBook> setDefault(@RequestBody AddressBook addressBook){
        //sql:update address_book set is_default=0 where user_id=?
        LambdaUpdateWrapper<AddressBook> lambdaUpdateWrapper=new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(AddressBook::getUserId,BaseContext
                .getCurrentId());
        lambdaUpdateWrapper.set(AddressBook::getIsDefault,0);
        addressBookService.update(lambdaUpdateWrapper);


        addressBook.setIsDefault(1);
        //sql:update address_book set is_default=1 where id=?
        addressBookService.updateById(addressBook);
        return Result.success(addressBook);
    }

    @GetMapping("default")
    public Result<AddressBook> getDefault(){
        LambdaQueryWrapper<AddressBook> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        lambdaQueryWrapper.eq(AddressBook::getIsDefault,1);
        AddressBook addressBook = addressBookService.getOne(lambdaQueryWrapper);

        if(addressBook!=null){
            return Result.success(addressBook);
        }
        return Result.error("未找到该对象");
    }

    @GetMapping("/list")
    public Result<List<AddressBook>> list(AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook:{}",addressBook);

        LambdaQueryWrapper<AddressBook> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(addressBook.getUserId()!=null,AddressBook::getUserId,addressBook.getUserId());
        lambdaQueryWrapper.orderByDesc(AddressBook::getUpdateTime);
        List<AddressBook> addressBookList = addressBookService.list(lambdaQueryWrapper);


            return Result.success(addressBookList);

    }
}
