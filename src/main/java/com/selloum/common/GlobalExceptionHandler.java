package com.selloum.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，底层基于代理
 */
//表示需要拦截哪些,拦截加了@RestController和@Controller
@ControllerAdvice(annotations = {RestController.class, Controller.class})
//要将返回的数据封装成json数据，所以使用@ResponseBody
//或者直接用@RestControllerAdvice替代上面的
//@RestControllerAdvice=ControllerAdvice+ResponseBody
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    /***
     * 异常处理方法
     * @param ex
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result<String> exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        log.error(ex.getMessage());

        //只处理账号冲突的Duplicate entry 'zhangsan' for key 'employee.idx_username'
        if(ex.getMessage().contains("Duplicate entry")) {
            //找到冲突账号
            String[] s = ex.getMessage().split(" ");
            String msg = s[2]+" 已存在";
            return Result.error(msg);

        }
        return Result.error("未知错误");
    }

    @ExceptionHandler(CustomException.class)
    public Result<String> exceptionHandler(CustomException ex) {
        log.error(ex.getMessage());
        return Result.error(ex.getMessage());
    }
}
