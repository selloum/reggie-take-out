package com.selloum.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.selloum.common.Result;
import com.selloum.entity.Employee;
import com.selloum.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    //登陆成功后要将员工id存入session，所以使用HttpServletRequest
    @PostMapping("/login")
    public Result<Employee> login(HttpServletRequest request, @RequestBody Employee employee){

        //1.md5加密密码
        String password=employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //2.根据用户名查询数据库
        //eq方法等于查询：select * from Employee where username=employee.getUsername();
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());

        Employee emp=employeeService.getOne(queryWrapper);
        //3.未查询到则返回登陆失败

        if(emp==null){
            return Result.error("登陆失败，用户不存在");
        }

        //4.查询到用户，进行密码比对，若不一致返回登陆失败结果
        if(!emp.getPassword().equals(password)){
            return Result.error("登陆失败,密码错误");
        }

        //5.密码比对成功，查看员工状态,0表示禁用
        if(emp.getStatus()==0){
            return Result.error("登陆失败,该用户已被禁用");
        }

        //6.登录成功，将用户id放入session
        request.getSession().setAttribute("employee",emp.getId());
        return Result.success(emp);

    }

    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request){
        //1.清理session中存的用户id
        request.getSession().removeAttribute("employee");
        return Result.success("退出成功");
    }

    //前端传来的数据是json格式，所以需要使用@RequestBody
    @PostMapping
    public Result<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工，员工信息：{}",employee.toString());

        //设置初始密码123456使用md5加密
        String password=DigestUtils.md5DigestAsHex("123456".getBytes());
        employee.setPassword(password);
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        //创建人，即此刻登录的用户,getAttribute返回object类型，需要墙砖
//        employee.setCreateUser((Long) request.getSession().getAttribute("employee"));
//        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));

        employeeService.save(employee);
        return Result.success("新增员工成功");
    }

    /***
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page> page(int page,int pageSize, String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);

        //1.构造分页构造器
        Page pageInfo=new Page(page,pageSize);


        //2.构造条件构造器
        LambdaQueryWrapper<Employee> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        //添加一个过滤条件like模糊匹配,name不为空时添加
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件
        lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);


        //3.执行查询
        employeeService.page(pageInfo, lambdaQueryWrapper);
        return Result.success(pageInfo);
    }

    //根据id修改员工信息
    //long类型的id19位。前端丢失精度，用扩展mvc
    @PutMapping
    public Result<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));
        employeeService.updateById(employee);
        return Result.success("修改成功");
    }

    @GetMapping("/{id}")
    //@PathVariable路径变量
    public Result<Employee> getById(@PathVariable Long id){

        log.info("根据id查询员工信息");
        Employee emp = employeeService.getById(id);
        if(emp!=null){
            return Result.success(emp);
        }
        return Result.error("没有查询到对应员工");

    }
}
