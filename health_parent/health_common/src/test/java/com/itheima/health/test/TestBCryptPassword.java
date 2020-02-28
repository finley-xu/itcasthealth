package com.itheima.health.test;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @ClassName TestPOI
 * @Description TODO
 * @Author ly
 * @Company 深圳黑马程序员
 * @Date 2020/2/19 10:52
 * @Version V1.0
 */
public class TestBCryptPassword {

    @Test
    public void bCryptPasword(){
        // 新增注册用户
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String s1 = bCryptPasswordEncoder.encode("123");
        System.out.println(s1);

        String s2 = bCryptPasswordEncoder.encode("123");
        System.out.println(s2);

        // 登录页面匹配，第一个参数，页面输入的密码；第二个参数，从数据库查询的密码
        boolean matches = bCryptPasswordEncoder.matches("1234", "$2a$10$2HXHm6LJtwVMZTDl/eKcZOEwUxhstckS4ace2fGbS2WurLT2TkzQO");
        System.out.println(matches);

    }
}
