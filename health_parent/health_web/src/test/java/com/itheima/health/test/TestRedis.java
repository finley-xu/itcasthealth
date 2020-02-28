package com.itheima.health.test;

import com.itheima.health.constant.RedisPicConstant;
import com.itheima.health.utils.QiniuUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.clients.jedis.JedisPool;

import java.util.Iterator;
import java.util.Set;

/**
 * @ClassName TestRedis
 * @Description TODO
 * @Author ly
 * @Company 深圳黑马程序员
 * @Date 2020/2/17 15:15
 * @Version V1.0
 */
// spring整合junit
@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = "classpath:spring-redis.xml")
public class TestRedis {

    @Autowired
    JedisPool jedisPool;

    // 测试比较Rediskey不一样的地方，删除七牛云上的图片
    @Test
    public void testRedisPic(){
        // 用于比较集合（注意：集合多的放置前面）
        Set<String> set = jedisPool.getResource().sdiff(RedisPicConstant.SETMEAL_PIC_RESOURCE, RedisPicConstant.SETMEAL_PIC_DB_RESOURCE);
        // 遍历
        Iterator<String> iterator = set.iterator();
        while(iterator.hasNext()){
            String img = iterator.next();
            System.out.println("需要删除的图片名称："+img);
            // 删除七牛云上的图片
            QiniuUtils.deleteFileFromQiniu(img);
            // 删除key值是setmeal_pic_resource的值
            jedisPool.getResource().srem(RedisPicConstant.SETMEAL_PIC_RESOURCE,img);
        }
    }
}
