package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.entity.Result;
import com.itheima.health.service.MemberService;
import com.itheima.health.service.SetmealService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName ReportContoller
 * @Description TODO
 * @Author ly
 * @Company 深圳黑马程序员
 * @Date 2020/2/13 16:04
 * @Version V1.0
 */
@RestController
@RequestMapping(value = "/report")
public class ReportContoller {

    @Reference// 订阅 dubbo注解
    MemberService memberService;

    @Reference
    SetmealService setmealService;


    // 统计报表（会员数量折线图统计）
    @RequestMapping(value = "/getMemberReport")
    public Result getMemberReport(){
        try {
            // 组织结果集
            /**
             * 返回Map<String,Object>
             map集合的key：                       map集合的value：
             months                               List<String>   -->['2019-06','2019-07']
             memberCount                          List<Integer> -->[10,35]
             */
            // 使用日历的工具类，统计过去12个月的时间
            Map<String,Object> map = new HashMap<>();
            // 存放到List<String> 对应key：months
            List<String> months = new ArrayList<>();
            // 获取Calendar对象
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH,-12); // 获取过去12个月，2019-2
            for (int i = 0; i < 12; i++) {
                // 过去的12个月，输出
                calendar.add(Calendar.MONTH,1); //2019-3
                Date date = calendar.getTime();
                String sDate = new SimpleDateFormat("yyyy-MM").format(date);
                months.add(sDate);
            }

            // 组织每个月查询的会员数量（sql：SELECT COUNT(*) FROM t_member WHERE  regTime<= '2019-04-31' ）
            List<Integer> memberCount = memberService.findCountByBeforeRegTime(months);

            // [2019-03, 2019-04, 2019-05, 2019-06, 2019-07, 2019-08, 2019-09, 2019-10, 2019-11, 2019-12, 2020-01, 2020-02]
            map.put("months",months);
            // [3, 4, 5, 7, ...]
            map.put("memberCount",memberCount);
            return new Result(true, MessageConstant.GET_MEMBER_NUMBER_REPORT_SUCCESS,map);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_MEMBER_NUMBER_REPORT_FAIL);
        }
    }

    // 统计报表（套餐预约占比饼形图统计）
    @RequestMapping(value = "/getSetmealReport")
    public Result getSetmealReport(){
        try {
            // 组织结果集
            /**
             * 返回Map<String,Object>
             map集合的key：                       map集合的value：
             setmealNames                          List<String>   -->['入职体检套餐','妇女节套餐']
             setmealCount                          List<Map<String,Object>> -->[
                                                                        {value: 335, name: '入职体检套餐'},
                                                                        {value: 310, name: '妇女节套餐'}
                                                                        ]
             */
            Map<String,Object> map = new HashMap<>();
            // 组织List<Map>
            List<Map> setmealCount = setmealService.findOrderCountBySetmealName();
            // 组织List<String>
            List<String> setmealNames = new ArrayList<>();
            // 遍历setmealCount
            if(setmealCount!=null && setmealCount.size()>0){
                for (Map setmealMap : setmealCount) {
                    String name = (String)setmealMap.get("name");
                    setmealNames.add(name);
                }
            }
            map.put("setmealNames",setmealNames);
            map.put("setmealCount",setmealCount);
            return new Result(true, MessageConstant.GET_MEMBER_NUMBER_REPORT_SUCCESS,map);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_MEMBER_NUMBER_REPORT_FAIL);
        }
    }

    /**
     * #################################################################################
         #对应SQL
         # 一：会员相关
         # reportDate:null, 时间（当前时间）
         # todayNewMember :0,：今天新增会员数
         SELECT COUNT(id) FROM t_member WHERE regTime = '2020-02-27'
         # totalMember :0,：总会员数
         SELECT COUNT(id) FROM t_member
         # thisWeekNewMember :0, :本周新增会员数（计算本周的周一）
         SELECT COUNT(id) FROM t_member WHERE regTime >= '2020-02-24'
         # thisMonthNewMember :0, ：本月新增会员数（计算本月的1号）
         SELECT COUNT(id) FROM t_member WHERE regTime >= '2020-02-01'
         # 二：预约订单相关
         # todayOrderNumber :0,：今天预约人数
         SELECT COUNT(id) FROM t_order WHERE orderDate = '2020-02-27'
         # todayVisitsNumber :0,：今天到诊人数
         SELECT COUNT(id) FROM t_order WHERE orderDate = '2020-02-27' AND orderStatus = '已到诊'
         # thisWeekOrderNumber :0, ：本周预约人数（计算本周的周一，计算本周的周日）
         SELECT COUNT(id) FROM t_order WHERE orderDate BETWEEN '2020-02-24' AND '2020-03-01'
         # thisWeekVisitsNumber :0, ：本周到诊人数（计算本周的周一，计算本周的周日）
         SELECT COUNT(id) FROM t_order WHERE orderDate BETWEEN '2020-02-24' AND '2020-03-01' AND orderStatus = '已到诊'
         # thisMonthOrderNumber :0, ：本月预约人数（计算本月的1号，本月的最后1天）
         SELECT COUNT(id) FROM t_order WHERE orderDate BETWEEN '2020-02-01' AND '2020-02-29'
         # thisMonthVisitsNumber :0, ：本月到诊人数（计算本月的1号，本月的最后1天）
         SELECT COUNT(id) FROM t_order WHERE orderDate BETWEEN '2020-02-01' AND '2020-02-29' AND orderStatus = '已到诊'
         # 三：套餐相关
         #hotSetmeal :[   # 热门套餐（预约最多的放置到最前面，显示最热门的4个）
         #{name:'阳光爸妈升级肿瘤12项筛查（男女单人）体检套餐',setmeal_count:200,proportion:0.222},
         #{name:'阳光爸妈升级肿瘤12项筛查体检套餐',setmeal_count:200,proportion:0.222}
         #]
         SELECT s.name,COUNT(*) setmeal_count,COUNT(*)/(SELECT COUNT(*) FROM t_order) proportion FROM t_order o,t_setmeal s
         WHERE o.setmeal_id = s.id
         GROUP BY s.name
         ORDER BY setmeal_count DESC
         LIMIT 0,4
     */
}
