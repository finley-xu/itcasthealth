package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.constant.RedisMessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.Member;
import com.itheima.health.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

/**
 * @ClassName LoginMobileController
 * @Description TODO
 * @Author ly
 * @Company 深圳黑马程序员
 * @Date 2020/2/20 14:20
 * @Version V1.0
 */
@RestController
@RequestMapping(value = "/login")
public class LoginMobileController {

    @Reference
    private MemberService memberService;

    @Autowired
    JedisPool jedisPool;

    // 手机快速登录
    @RequestMapping(value = "/check")
    public Result check(@RequestBody Map map, HttpServletResponse response){
        // 1：获取手机号和验证码
        String telepone = (String)map.get("telephone");
        String validateCode = (String)map.get("validateCode");
        // 2：使用手机号，从Redis中获取验证码
        String redisValidateCode = jedisPool.getResource().get(telepone+ RedisMessageConstant.SENDTYPE_LOGIN);
        // 3：比对验证码，如果验证码没有匹配，提示“验证码输入有误”
        if(redisValidateCode == null || !redisValidateCode.equals(validateCode)){
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }
        // 4：使用手机号作为查询条件，判断当前手机号是否是会员
        Member member = memberService.findMemberByTelephone(telepone);
        // 此时不是会员，需要注册会员
        if(member==null){
            member = new Member();
            member.setPhoneNumber(telepone); // 当前手机号
            member.setRegTime(new Date()); // 注册时间，当前时间
            memberService.add(member);
        }
        // 5：将手机号存放到Cookie，保存用户信息，方便调用其他服务。
        Cookie cookie = new Cookie("login_member_telephone_85",telepone);
        cookie.setPath("/"); // 有效路径
        cookie.setMaxAge(30*24*60*60); // 有效时间，单位秒
        response.addCookie(cookie);
        return new Result(true,MessageConstant.LOGIN_SUCCESS);
    }

}
