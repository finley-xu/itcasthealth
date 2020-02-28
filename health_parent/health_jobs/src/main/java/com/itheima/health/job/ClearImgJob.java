package com.itheima.health.job;

import com.itheima.health.constant.RedisPicConstant;
import com.itheima.health.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.JedisPool;

import java.util.Iterator;
import java.util.Set;

/**
 * @ClassName ClearImgJob
 * @Description TODO
 * @Author ly
 * @Company 深圳黑马程序员
 * @Date 2020/2/19 10:24
 * @Version V1.0
 */
public class ClearImgJob {

    // 注意：注入JedisPool
    @Autowired // 注入
    JedisPool jedisPool;

    public void clearImg(){
        // 用于比较集合（注意：集合多的放置前面）
        Set<String> set = jedisPool.getResource().sdiff(RedisPicConstant.SETMEAL_PIC_RESOURCE, RedisPicConstant.SETMEAL_PIC_DB_RESOURCE);
        // 遍历
        Iterator<String> iterator = set.iterator();
        while(iterator.hasNext()) {
            String img = iterator.next();
            System.out.println("需要删除的图片名称：" + img);
            // 删除七牛云上的图片
            QiniuUtils.deleteFileFromQiniu(img);
            // 删除key值是setmeal_pic_resource的值
            jedisPool.getResource().srem(RedisPicConstant.SETMEAL_PIC_RESOURCE, img);
        }
    }
}
