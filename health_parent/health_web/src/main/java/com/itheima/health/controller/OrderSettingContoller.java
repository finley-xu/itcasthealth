package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.OrderSetting;
import com.itheima.health.service.OrderSettingService;
import com.itheima.health.utils.POIUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName OrderSettingContoller
 * @Description TODO
 * @Author ly
 * @Company 深圳黑马程序员
 * @Date 2020/2/13 16:04
 * @Version V1.0
 */
@RestController
@RequestMapping(value = "/ordersetting")
public class OrderSettingContoller {

    @Reference// 订阅 dubbo注解
    OrderSettingService orderSettingService;

    // 批量导入预约设置（通过Excel完成）
    @RequestMapping(value = "/upload")
    public Result upload(MultipartFile excelFile){
        try {
            // 读取Excel文件，并批量导入到数据库
            List<String[]> list = POIUtils.readExcel(excelFile);
            // 将List<String[]>集合，转换成List<OrderSetting>
            if(list!=null && list.size()>0){
                List<OrderSetting> orderSettingList = new ArrayList<>();
                for (String[] strings : list) {
                    OrderSetting orderSetting = new OrderSetting(new Date(strings[0]),Integer.parseInt(strings[1]));
                    orderSettingList.add(orderSetting);
                }
                // 批量导入
                orderSettingService.addList(orderSettingList);
            }
            return new Result(true, MessageConstant.IMPORT_ORDERSETTING_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.IMPORT_ORDERSETTING_FAIL);
        }
    }

    // 根据传递的年月（2020-2），获取当前月对应的日期数据
    @RequestMapping(value = "/findOrderSettingMapByMonth")
    public Result findOrderSettingMapByMonth(String date){
        try {
            List<Map<String,Object>> list = orderSettingService.findOrderSettingMapByMonth(date);
            return new Result(true, MessageConstant.GET_ORDERSETTING_SUCCESS,list);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_ORDERSETTING_FAIL);
        }
    }

    // 根据传递的当前时间年月日（2020-02-21），更新最多预约人数number
    @RequestMapping(value = "/updateNumberByOrderDate")
    public Result updateNumberByOrderDate(@RequestBody OrderSetting orderSetting){
        try {
            orderSettingService.updateNumberByOrderDate(orderSetting);
            return new Result(true, MessageConstant.ORDERSETTING_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.ORDERSETTING_FAIL);
        }
    }
}
