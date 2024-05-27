package com.selloum.common;


import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用返回结果类，服务端响应的数据都会封装成Result对象
 */
@Data
public class Result<T> {

    private Integer code;///编码：1成功0失败

    private String msg;///错误的信息

    private T data;//数据

    private Map map=new HashMap();//动态数据

    public static <T> Result<T> success(T data){
        Result<T> result=new Result<T>();
        result.code=1;
        result.data=data;
        return result;
    }

    public static <T> Result<T> error(String msg){
        Result<T> result=new Result<T>();
        result.msg = msg;
        result.code=0;
        return result;
    }

    public Result<T> add(String key,Object value){
        this.map.put(key,value);
        return this;
    }
}
