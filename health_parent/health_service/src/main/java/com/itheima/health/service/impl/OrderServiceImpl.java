package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.dao.MemberDao;
import com.itheima.health.dao.OrderDao;
import com.itheima.health.dao.OrderSettingDao;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.Member;
import com.itheima.health.pojo.Order;
import com.itheima.health.pojo.OrderSetting;
import com.itheima.health.service.OrderService;
import com.itheima.health.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName OrderServiceImpl
 * @Description TODO
 * @Author ly
 * @Company 深圳黑马程序员
 * @Date 2020/2/13 16:03
 * @Version V1.0
 */
@Service // dubbo提供
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderDao orderDao;

    @Autowired
    OrderSettingDao orderSettingDao;

    @Autowired
    MemberDao memberDao;


    /**
     * 对应sql语句：
         * # 根据预约时间，查询预约设置对象
         SELECT * FROM t_ordersetting WHERE orderDate = '2020-02-29'

         # 使用手机号，查询会员
         SELECT * FROM t_member WHERE phoneNumber = '13212341234'

         # 使用会员id，预约时间，套餐id，查询预约订单表
         SELECT * FROM t_order WHERE member_id = 1 AND orderDate = '2020-02-29' AND setmeal_id = 6

         # 新增会员表
         INSERT INTO t_member() VALUES()

         # 后续还有新增预约订单表
         INSERT INTO t_order() VALUES()

         # 根据预约设置时间，更新实际预约人数，使其+1
         UPDATE t_ordersetting SET reservations = reservations+1 WHERE orderDate = '2020-02-29'
     */
    /**
     * Service如何抛出异常
     * (1)
     * try{
     *
     * }.catch(){
     *    // 可以打印，但是事务失效
     *    // 向上抛出异常（对应声明式事务出来来说，抛出运行时异常，默认运行是异常可以被捕获）
     *    throw new RuntimeException("抛出运行时异常");
     * }
     * (2)
     * Throws Exception，在类上的方法上，抛出异常，事务的代理对象中可以被捕获 / Throw
     * @param map
     * @return
     */
    @Override
    public Result orderSubmit(Map map) {

        try {
            // 获取预约设置时间
            String orderDate = (String)map.get("orderDate");
            // 将String类型转换成日期类型（预约时间）
            Date date = DateUtils.parseString2Date(orderDate);
            // 1：使用预约时间，查询预约设置表，返回预约设置对象
            OrderSetting orderSetting = orderSettingDao.findOrderSettingByOrderDate(date);
            // 2：判断，如果预约设置为空，提示“当前预约时间，不能进行体检预约”
            if(orderSetting == null){
                return new Result(false,MessageConstant.SELECTED_DATE_CANNOT_ORDER);
            }
            // 3：判断，如果已经预约人数（reservations）>最多预约人数（number），提示“预约已满”
            int number = orderSetting.getNumber();
            int reservations = orderSetting.getReservations();
            if(reservations>=number){
                return new Result(false,MessageConstant.ORDER_FULL);
            }
            // 4：使用手机号，查询会员表，返回会员对象，用来判断当前手机号是否注册会员
            String telepone = (String)map.get("telephone");
            Member member = memberDao.findMemberByTelephone(telepone);
            // 5：如果Member对象不为空，说明他是会员，需要判断，当前会员是否重复预约
            // 使用查询条件会员id、预约时间、套餐id查询预约表，返回查询的List，使用List是否为空，进行判断同一个会员，同一时间，不能预约同一套餐
            if(member!=null){
                // 会员id
                Integer memberId = member.getId();
                // 套餐id
                String setmealId = (String)map.get("setmealId");
                Order order = new Order(memberId,date,null,null,Integer.parseInt(setmealId));
                List<Order> list = orderDao.findOrderListByCondition(order);
                if(list!=null && list.size()>0){
                    return new Result(false,MessageConstant.HAS_ORDERED);
                }
            }
            // 6：如果Member对象为空，说明他不是会员，注册新的会员
            else{
                member = new Member();
                member.setName((String)map.get("name"));
                member.setSex((String)map.get("sex"));
                member.setIdCard((String)map.get("idCard"));
                member.setPhoneNumber(telepone);
                member.setRegTime(new Date()); // 注册时间（当前日期）
                memberDao.add(member);
            }
            // 7：更新预约设置表，根据预约时间，更新reservations字段，将字段+1
            orderSettingDao.updateReservationsByOrderDate(date);

            // 8：新增预约表，保存一条预约的数据
            Order order = new Order(member.getId(),date,(String)map.get("orderType"),Order.ORDERSTATUS_NO,Integer.parseInt((String)map.get("setmealId")));
            orderDao.add(order);
            return new Result(true, MessageConstant.ORDER_SUCCESS,order);// 预约成功，传递订单（因为页面要传递订单id，到成功页面）
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常，在控制台可以看到异常信息
            throw new RuntimeException("抛出运行时异常");
            //return new Result(false, MessageConstant.ORDER_FAIL);
        }
    }

    @Override
    public Map findById(Integer id) {
        Map map = orderDao.findById(id);
        if(map!=null && map.size()>0){
            Date date = (Date)map.get("orderDate");
            try {
                String orderDate = DateUtils.parseDate2String(date);
                map.put("orderDate",orderDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }
}
