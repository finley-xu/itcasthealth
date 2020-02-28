package com.itheima.health.test;

import com.itheima.health.utils.ValidateCodeUtils;
import org.junit.Test;

/**
 * @ClassName TestValidateCode
 * @Description TODO
 * @Author ly
 * @Company 深圳黑马程序员
 * @Date 2020/2/20 11:50
 * @Version V1.0
 */
public class TestValidateCode {

    @Test
    public void testCode(){
        // 生成4位
        Integer code4 = ValidateCodeUtils.generateValidateCode(4);
        System.out.println(code4);
        // 生成6位
        Integer code6 = ValidateCodeUtils.generateValidateCode(6);
        System.out.println(code6);

        // 生成字符串
        String codeStr4 = ValidateCodeUtils.generateValidateCode4String(4);
        System.out.println(codeStr4);
    }
}
