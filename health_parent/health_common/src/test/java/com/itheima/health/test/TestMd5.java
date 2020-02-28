package com.itheima.health.test;

import com.itheima.health.utils.MD5Utils;
import org.junit.Test;

/**
 * @ClassName TestPOI
 * @Description TODO
 * @Author ly
 * @Company 深圳黑马程序员
 * @Date 2020/2/19 10:52
 * @Version V1.0
 */
public class TestMd5 {

    @Test
    public void md5(){
        String s = MD5Utils.md5("123");// 对明文密码123加密
        System.out.println(s);
        String s1 = MD5Utils.md5("123");// 对明文密码123加密
        System.out.println(s1);

        String s2 = MD5Utils.md5("admin123");// 对明文密码123加密
        System.out.println(s2);

        String s3 = MD5Utils.md5("adminadmin123");// 对明文密码123加密
        System.out.println(s3);

        String s4 = MD5Utils.md5("adminadminadminadminadmin123");// 对明文密码123加密
        System.out.println(s4);
    }
}
