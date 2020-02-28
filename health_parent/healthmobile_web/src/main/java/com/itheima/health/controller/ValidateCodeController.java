package com.itheima.health.controller;

import com.itheima.health.constant.MessageConstant;
import com.itheima.health.constant.RedisMessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

/**
 * @ClassName ValidateCodeController
 * @Description TODO
 * @Author ly
 * @Company 深圳黑马程序员
 * @Date 2020/2/20 14:20
 * @Version V1.0
 */
@RestController
@RequestMapping(value = "/validateCode")
public class ValidateCodeController {

    @Autowired
    JedisPool jedisPool;

    // 给当前手机号发送验证码，业务：“体检预约”
    @RequestMapping(value = "/send4Order")
    public Result send4Order(String telephone){
        try {
            //1：获取手机号，生成4位验证码（1122），发送短信（手机1122）
            Integer code4 = ValidateCodeUtils.generateValidateCode(4);
            // SMSUtils.sendShortMessage(telephone,code4.toString());
            System.out.println("短信接收的验证码是："+code4);
            /**
             * 2：将手机号和验证码存放到redis中
             key                              value                     失效时间（秒）
             13212341234001                  1122                          5*60
             */
            jedisPool.getResource().setex(telephone+ RedisMessageConstant.SENDTYPE_ORDER,5*60,code4.toString());
            return new Result(true,MessageConstant.SEND_VALIDATECODE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.SEND_VALIDATECODE_FAIL);
        }
    }

    // 给当前手机号发送验证码，业务：“手机快速登录”
    @RequestMapping(value = "/send4Login")
    public Result send4Login(String telephone){
        try {
            //1：获取手机号，生成4位验证码（1122），发送短信（手机1122）
            Integer code4 = ValidateCodeUtils.generateValidateCode(4);
            //SMSUtils.sendShortMessage(telephone,code4.toString());
            System.out.println("短信接收的验证码是："+code4);
            /**
             * 2：将手机号和验证码存放到redis中
             key                              value                     失效时间（秒）
             13212341234002                  1122                          5*60
             */
            jedisPool.getResource().setex(telephone+ RedisMessageConstant.SENDTYPE_LOGIN,5*60,code4.toString());
            return new Result(true,MessageConstant.SEND_VALIDATECODE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.SEND_VALIDATECODE_FAIL);
        }
    }
}
