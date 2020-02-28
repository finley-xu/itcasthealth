package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.service.PermissionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName RoleContoller
 * @Description TODO
 * @Author ly
 * @Company 深圳黑马程序员
 * @Date 2020/2/13 16:04
 * @Version V1.0
 */
@RestController
@RequestMapping(value = "/permission")
public class PermissionContoller {

    @Reference// 订阅 dubbo注解
    PermissionService permissionService;
    
}
