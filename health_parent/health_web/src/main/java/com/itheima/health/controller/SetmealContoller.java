package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.constant.RedisPicConstant;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.Setmeal;
import com.itheima.health.service.SetmealService;
import com.itheima.health.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.UUID;

/**
 * @ClassName SetmealContoller
 * @Description TODO
 * @Author ly
 * @Company 深圳黑马程序员
 * @Date 2020/2/13 16:04
 * @Version V1.0
 */
@RestController
@RequestMapping(value = "/setmeal")
public class SetmealContoller {

    @Reference// 订阅 dubbo注解
    SetmealService setmealService;

    @Autowired
    JedisPool jedisPool;

    // 上传
    @RequestMapping(value = "/upload")
    public Result upload(MultipartFile imgFile){
        try {
            // * 实现将图片存放到七牛云上
            // 参数一：字节数组（springmvc的方式），使用imgFile.getBytes()
            // 参数二：上传文件名（唯一）
            // 获取上传的文件名（1.jpg）
            String originalFilename = imgFile.getOriginalFilename();
            // 使用UUID的方式生成文件名
            String fileName = UUID.randomUUID()+originalFilename.substring(originalFilename.lastIndexOf("."));
            // * 实现将图片存放到七牛云上
            QiniuUtils.upload2Qiniu(imgFile.getBytes(),fileName);
            // （1）Redis的集合存储，上传图片的时候，将图片名称，存放到Redis中一份（key值setmeal_pic_resource），然后再上传到七牛云上
            jedisPool.getResource().sadd(RedisPicConstant.SETMEAL_PIC_RESOURCE,fileName);
            // UUID的方式生成文件名，并返回
            return new Result(true, MessageConstant.PIC_UPLOAD_SUCCESS,fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.PIC_UPLOAD_FAIL);
        }
    }

    // 保存
    @RequestMapping(value = "/add")
    public Result add(@RequestBody Setmeal setmeal,Integer [] checkgroupIds){
        try {
            setmealService.add(setmeal,checkgroupIds);
            return new Result(true, MessageConstant.ADD_SETMEAL_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.ADD_SETMEAL_FAIL);
        }
    }

    // 分页查询
    @RequestMapping(value = "/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){

        PageResult pageResult = setmealService.findPage(queryPageBean.getCurrentPage(),
                            queryPageBean.getPageSize(),
                            queryPageBean.getQueryString());
        return pageResult;
    }

    // ID查询
    @RequestMapping(value = "/findById")
    public Result findById(Integer id){
        Setmeal setmeal = setmealService.findById(id);
        if(setmeal!=null){
            return new Result(true,MessageConstant.QUERY_SETMEAL_SUCCESS,setmeal);
        }
        else{
            return new Result(false,MessageConstant.QUERY_SETMEAL_FAIL);
        }
    }

    // 使用套餐ID，查询检查组ID的集合
    @RequestMapping(value = "/findCheckGroupIdsBySetmealId")
    public List<Integer> findCheckGroupIdsBySetmealId(Integer id){
        List<Integer> list = setmealService.findCheckGroupIdsBySetmealId(id);
        return list;
    }

    // 编辑保存
    @RequestMapping(value = "/edit")
    public Result edit(@RequestBody Setmeal setmeal, Integer [] checkgroupIds){
        try {
            setmealService.edit(setmeal,checkgroupIds);
            return new Result(true, MessageConstant.EDIT_SETMEAL_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.EDIT_SETMEAL_FAIL);
        }
    }

    // 删除
    @RequestMapping(value = "/delete")
    public Result delete(Integer id){
        try {
            setmealService.delete(id);
            return new Result(true, MessageConstant.DELETE_SETMEAL_SUCCESS);
        }catch (RuntimeException e) {
            e.printStackTrace();
            return new Result(false, e.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.DELETE_SETMEAL_FAIL);
        }
    }
}
