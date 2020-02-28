package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName UserContoller
 * @Description TODO
 * @Author ly
 * @Company 深圳黑马程序员
 * @Date 2020/2/13 16:04
 * @Version V1.0
 */
@RestController
@RequestMapping(value = "/user")
public class UserContoller {

    @Reference// 订阅 dubbo注解
    UserService userService;

    // 使用SpringSecurity，获取当前登录的用户信息
    @RequestMapping(value = "/findUsername")
    public Result findUsername(){
        try {
            // 从SpringSecurity中获取用户信息
            User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            // 返回用户名，返回用户姓名，用户的地址...
            // com.itheima.health.pojo.User u = userService.findUserByUsername(user.getUsername());
            return new Result(true, MessageConstant.GET_USERNAME_SUCCESS,user.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_USERNAME_FAIL);
        }

    }
}
