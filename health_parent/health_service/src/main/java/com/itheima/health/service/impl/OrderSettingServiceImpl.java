package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.health.dao.OrderSettingDao;
import com.itheima.health.pojo.OrderSetting;
import com.itheima.health.service.OrderSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName OrderSettingServiceImpl
 * @Description TODO
 * @Author ly
 * @Company 深圳黑马程序员
 * @Date 2020/2/13 16:03
 * @Version V1.0
 */
@Service // dubbo提供
@Transactional
public class OrderSettingServiceImpl implements OrderSettingService {

    @Autowired
    OrderSettingDao orderSettingDao;

    @Override
    public void addList(List<OrderSetting> orderSettingList) {
        // 批量导入
        if(orderSettingList!=null && orderSettingList.size()>0){
            for (OrderSetting orderSetting : orderSettingList) {
                // 判断当前预约设置时间是否已经存在，如果已经存在，执行更新；如果不存在，再执行新增
                // 1：使用预约设置时间，查询预约设置，判断是否存在
                long count = orderSettingDao.findOrderSettingCountByOrderDate(orderSetting.getOrderDate());
                // 2：如果已经存在，执行更新
                if(count>0){
                    // 根据预约设置时间(orderDate字段)，更新最多预约人数(number)
                    orderSettingDao.updateNumberByOrderDate(orderSetting);
                }
                // 3：如果不存在，再执行新增
                else{
                    // 保存预约设置表
                    orderSettingDao.add(orderSetting);
                }

            }
        }
    }

    @Override
    public List<Map<String,Object>> findOrderSettingMapByMonth(String date) {
        // 根据当前年月，获取日期（也可以完成）
        // 开始时间
        String beginDate = date+"-1";
        // 结束时间
        String endDate = date+"-31";
        // 组织查询条件
        Map paramsMap = new HashMap();
        paramsMap.put("beginDate",beginDate);
        paramsMap.put("endDate",endDate);
        // 使用查询条件完成查询
        List<OrderSetting> list = orderSettingDao.findOrderSettingByMonthBetween(paramsMap);
        // 组织需要返回的数据
        List<Map<String,Object>> mapList = new ArrayList<>();
        if(list!=null && list.size()>0){
            for (OrderSetting orderSetting : list) {
                Map<String,Object> map = new HashMap<>();
                map.put("date",orderSetting.getOrderDate().getDate()); // 获取当前日期（1-31）
                map.put("number",orderSetting.getNumber());
                map.put("reservations",orderSetting.getReservations());
                mapList.add(map);
            }
        }
        return mapList;
    }

    @Override
    public void updateNumberByOrderDate(OrderSetting orderSetting) {
        // 判断当前预约设置时间是否已经存在，如果已经存在，执行更新；如果不存在，再执行新增
        // 1：使用预约设置时间，查询预约设置，判断是否存在
        long count = orderSettingDao.findOrderSettingCountByOrderDate(orderSetting.getOrderDate());
        // 2：如果已经存在，执行更新
        if(count>0){
            // 根据预约设置时间(orderDate字段)，更新最多预约人数(number)
            orderSettingDao.updateNumberByOrderDate(orderSetting);
        }
        // 3：如果不存在，再执行新增
        else{
            // 保存预约设置表
            orderSettingDao.add(orderSetting);
        }
    }
}
