package com.selloum.controller;

import com.selloum.common.BaseContext;
import com.selloum.common.Result;
import com.selloum.entity.Orders;
import com.selloum.service.OrderDetailService;
import com.selloum.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;


    @Autowired
    private OrderDetailService orderDetailService;

    /***
     * 用户下单
     * 涉及两个数据表，order order_detail
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public Result<String> submit(@RequestBody Orders orders){
        log.info("订单信息：{}",orders);
        orderService.submit(orders);
        return Result.success("下单成功");
    }
}
