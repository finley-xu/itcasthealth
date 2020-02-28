package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.constant.RedisMessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.Order;
import com.itheima.health.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import java.util.Map;

/**
 * @ClassName OrderMobileController
 * @Description TODO
 * @Author ly
 * @Company 深圳黑马程序员
 * @Date 2020/2/20 14:20
 * @Version V1.0
 */
@RestController
@RequestMapping(value = "/order")
public class OrderMobileController {

    @Reference
    private OrderService orderService;

    @Autowired
    JedisPool jedisPool;

    // 体检预约保存
    @RequestMapping(value = "/submit")
    public Result submit(@RequestBody Map map){
        // 1：获取手机号和验证码
        String telepone = (String)map.get("telephone");
        String validateCode = (String)map.get("validateCode");
        // 2：使用手机号，从Redis中获取验证码
        String redisValidateCode = jedisPool.getResource().get(telepone+ RedisMessageConstant.SENDTYPE_ORDER);
        // 3：比对验证码，如果验证码没有匹配，提示“验证码输入有误”
        if(redisValidateCode == null || !redisValidateCode.equals(validateCode)){
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }
        // 4：指定预约类型，预约类型是微信预约（map存储值）
        map.put("orderType", Order.ORDERTYPE_WEIXIN);
        Result result = null;
        try {
            result = orderService.orderSubmit(map);
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
        return result;
    }

    // 使用订单id，查询订单的详情信息
    @RequestMapping(value = "/findById")
    public Result findById(Integer id){
        Map map = orderService.findById(id);
        if(map!=null && map.size()>0){
            return new Result(true,MessageConstant.ORDER_SUCCESS,map);
        }else{
            return new Result(false,MessageConstant.ORDER_FAIL);
        }
    }

}
