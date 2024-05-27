package com.selloum.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.selloum.common.Result;
import com.selloum.entity.User;
import com.selloum.service.UserService;
import com.selloum.utils.SMSUtils;
import com.selloum.utils.ValidateCodeUtils;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public Result<String> sendMsg(@RequestBody User user, HttpSession session){

        //获取手机号
        String phone=user.getPhone();

        if(StringUtils.isNotEmpty(phone)){
            //生成随位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code:{}",code);
            //调用阿里云提供的短信服务API发送短信

//            SMSUtils.sendMessage("阿里云短信测试","SMS_154950909","17766337542",code);

            //将生成的验证码保存到session
            session.setAttribute(phone,code);

            return Result.success("手机验证码短信发送成功");
        }


        return Result.error("短信发送失败");
    }

    /***
     *
     * 移动端用户登录
     *用map接受界面数据phone喝验证码
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public Result<User> login(@RequestBody Map map, HttpSession session){

        log.info(map.toString());
        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //和session中的验证码比对，成功则登录成功
        Object codeInSeesion = session.getAttribute(phone);
        if(codeInSeesion!=null&&codeInSeesion.toString().equals(code)){
            //     比对成功则登录成功

            //判断手机号对应用户是否位新用户，若是，则完成注册
            LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(lambdaQueryWrapper);
            if(user==null){
                user=new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            return Result.success(user);
        }


        return Result.error("短信发送失败");
    }
}
