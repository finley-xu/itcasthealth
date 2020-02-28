package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.constant.RedisPicConstant;
import com.itheima.health.dao.CheckGroupDao;
import com.itheima.health.dao.CheckItemDao;
import com.itheima.health.dao.SetmealDao;
import com.itheima.health.entity.PageResult;
import com.itheima.health.pojo.Setmeal;
import com.itheima.health.service.SetmealService;
import com.itheima.health.utils.QiniuUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import redis.clients.jedis.JedisPool;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName SetmealServiceImpl
 * @Description TODO
 * @Author ly
 * @Company 深圳黑马程序员
 * @Date 2020/2/13 16:03
 * @Version V1.0
 */
@Service // dubbo提供
@Transactional
public class SetmealServiceImpl implements SetmealService {

    // 套餐
    @Autowired
    SetmealDao setmealDao;

    // 检查组
    @Autowired
    CheckGroupDao checkGroupDao;

    // 检查项
    @Autowired
    CheckItemDao checkItemDao;

    @Autowired
    JedisPool jedisPool;

    // 保存的功能
    @Override
    public void add(Setmeal setmeal, Integer[] checkgroupIds) {
        // 1：保存套餐
        setmealDao.add(setmeal);
        // 2：建立套餐和检查组的关联关系，向中间表保存数据
        setSetmealAndCheckGruop(setmeal.getId(),checkgroupIds);
        // （2）Redis的集合存储，保存套餐数据图片的时候，将图片名称，存放到Redis中一份（key值setmeal_pic_db_resource），然后在保存到数据库
        jedisPool.getResource().sadd(RedisPicConstant.SETMEAL_PIC_DB_RESOURCE,setmeal.getImg());

        // 生成静态页面（通过WEB-INF/ftl/下的flt文件）（输出到healthmobile_web下的webapp/pages下）
        this.generateMobileStaticHtml();
    }

    // 生成静态页面（通过WEB-INF/ftl/下的flt文件）（输出到healthmobile_web下的webapp/pages下）
    private void generateMobileStaticHtml() {
        // 查询所有套餐（从数据库查询）
        List<Setmeal> list = this.findAll();
        // 生成套餐列表的静态页面
        this.generateMobileSetmealListStaticHtml(list);
        // 生成套餐详情的静态页面
        this.generateMobileSetmealDetailStaticHtml(list);
    }

    // 生成套餐列表的静态页面
    private void generateMobileSetmealListStaticHtml(List<Setmeal> list) {
        Map<String,Object> map = new HashMap<>();
        map.put("setmealList",list);
        // 生成静态页面（参数1：静态页面的ftl文件名，参数2：静态页面的名称，参数三：map）
        useFreeMarkerGenerateHtml("mobile_setmeal.ftl","m_setmeal.html",map);
    }

    // 生成套餐详情的静态页面
    private void generateMobileSetmealDetailStaticHtml(List<Setmeal> list) {
        if(list!=null && list.size()>0){
            for (Setmeal setmeal : list) {
                Map<String,Object> map = new HashMap<>();
                map.put("setmeal",this.findById(setmeal.getId())); // 使用套餐id，查询套餐的详情（包括检查组的集合和检查项的集合）
                // 生成静态页面（参数1：静态页面的ftl文件名，参数2：静态页面的名称，参数三：map）
                useFreeMarkerGenerateHtml("mobile_setmeal_detail.ftl","setmeal_detail_"+setmeal.getId()+".html",map);
            }
        }
    }

    @Autowired
    FreeMarkerConfigurer freeMarkerConfigurer;

    @Value("${output_path}")
    private String output_path;  // 等同于加载D:\\ideaProjects\\85\\health_parent\\healthmobile_web\\src\\main\\webapp\\pages

    // 生成静态页面（参数1：静态页面的ftl文件名，参数2：静态页面的名称，参数三：map）
    private void useFreeMarkerGenerateHtml(String mobile_ftl, String mobile_html, Map<String, Object> map) {
        //  第一步：创建一个 Configuration 对象
        Configuration configuration = freeMarkerConfigurer.getConfiguration();
        Writer writer = null;
        try {
            // 第二步：加载一个模板，创建一个模板对象。
            Template template = configuration.getTemplate(mobile_ftl);
            // 第三步：创建Writer对象，加载D:\\ideaProjects\\85\\health_parent\\healthmobile_web\\src\\main\\webapp\\pages\\m_setmeal.html
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(output_path+"\\"+mobile_html))));
            // 第四步：调用模板对象的 process 方法输出文件（格式，定义html）。
            template.process(map,writer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //第八步：关闭流。
            try {
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public PageResult findPage(Integer currentPage, Integer pageSize, String queryString) {
        PageHelper.startPage(currentPage,pageSize);
        Page<Setmeal> page = setmealDao.findPage(queryString);
        return new PageResult(page.getTotal(),page.getResult());
    }


    /**
     #使用套餐id，查询检查组的集合（嵌套查询in）
     SELECT * FROM t_checkgroup WHERE id IN (SELECT checkgroup_id FROM t_setmeal_checkgroup WHERE setmeal_id = 6)

     #使用套餐id，查询检查组的集合（联合查询）
     SELECT cg.* FROM t_checkgroup cg,t_setmeal_checkgroup s WHERE cg.id = s.checkgroup_id AND s.setmeal_id = 6

     ****************************************************************************************************
     # 使用检查组的id，查询检查项的集合（嵌套查询in）
     SELECT * FROM t_checkitem WHERE id IN (SELECT checkitem_id FROM t_checkgroup_checkitem WHERE checkgroup_id = 5)

     # 使用检查组的id，查询检查项的集合（联合查询）
     SELECT ci.* FROM t_checkitem ci,t_checkgroup_checkitem c WHERE ci.id = c.checkitem_id AND c.checkgroup_id = 5
     */
//    @Override
//    public Setmeal findById(Integer id) {
//        // 1：使用id查询套餐信息
//        Setmeal setmeal = setmealDao.findById(id);
//        // 2：查询检查组集合的信息，封装到套餐对象中的checkGroups的集合属性中
//        List<CheckGroup> checkGroups = checkGroupDao.findCheckGroupListBySetmealId(setmeal.getId());
//        // 3：查询检查项集合的信息，封装到检查组对象中的checkItems的集合属性中
//        if(checkGroups!=null && checkGroups.size()>0){
//            for (CheckGroup checkGroup : checkGroups) {
//                List<CheckItem> checkItems = checkItemDao.findCheckItemListByCheckGroupId(checkGroup.getId());
//                checkGroup.setCheckItems(checkItems); // 封装检查组中的检查项集合
//            }
//        }
//        setmeal.setCheckGroups(checkGroups); // 封装套餐中的检查组集合
//        return setmeal;
//    }

    // 使用resultMap映射方式（企业中被推广）
    @Override
    public Setmeal findById(Integer id) {
        // 1：使用id查询套餐信息
        Setmeal setmeal = setmealDao.findById(id);
        return setmeal;
    }

    @Override
    public List<Integer> findCheckGroupIdsBySetmealId(Integer id) {
        return setmealDao.findCheckGroupIdsBySetmealId(id);
    }

    // 编辑保存套餐
    @Override
    public void edit(Setmeal setmeal, Integer[] checkgroupIds) {
        // 建议最后操作数据库，先操作其他服务器内容（删除七牛云上的图片）
        // 判断是否改变的图片名称
        // (1)使用套餐id，查询套餐
        Setmeal setmeal_db = setmealDao.findById(setmeal.getId());
        // (2)获取数据库的套餐图片名称
        String img_db = setmeal_db.getImg();
        // (3)判断是否改变图片名称
        // 如果改变套餐图片的名称，需要做处理
        if(setmeal.getImg()!=null && !setmeal.getImg().equals(img_db)){
            // 删除之前七牛云上的存放图片
            QiniuUtils.deleteFileFromQiniu(img_db);
            // 删除Redis中key值是SETMEAL_PIC_RESOURCE的图片名称
            jedisPool.getResource().srem(RedisPicConstant.SETMEAL_PIC_RESOURCE,img_db);
            // 删除Redis中key值是SETMEAL_PIC_DB_RESOURCE的图片名称
            jedisPool.getResource().srem(RedisPicConstant.SETMEAL_PIC_DB_RESOURCE,img_db);
            // 添加Redis中key值是SETMEAL_PIC_DB_RESOURCE的新的图片名称
            jedisPool.getResource().sadd(RedisPicConstant.SETMEAL_PIC_DB_RESOURCE,setmeal.getImg());
        }
        // 1：编辑保存套餐
        setmealDao.edit(setmeal);
        // 2：删除套餐和检查组中间表的数据
        setmealDao.deleteSetmealAndCheckGroupBySetmealId(setmeal.getId());
        // 3：重新新增套餐和检查组中间表的数据，建立关联关系
        this.setSetmealAndCheckGruop(setmeal.getId(),checkgroupIds);

        // 生成静态页面（通过WEB-INF/ftl/下的flt文件）（输出到healthmobile_web下的webapp/pages下）
        this.generateMobileStaticHtml();
    }

    // 删除套餐
    @Override
    public void delete(Integer id) {
        // 判断当前套餐是否和检查组之间存在关联关系
        Long count = setmealDao.findSetmealAndCheckGroupCountBySetmealId(id);
        // 查询获取到数据
        if(count>0){
            throw new RuntimeException(MessageConstant.GET_SETMEALANDCHECKGROUPERROR);
        }
        // 如果可以删除
        // 先删除七牛云上的图片
        // 使用套餐id，查询套餐
        Setmeal setmeal_db = setmealDao.findById(id);
        // 获取删除的图片名称
        String img_db = setmeal_db.getImg();
        // 七牛云删除图片
        if(img_db!=null && !"".equals(img_db)){
            // 删除七牛云上图片
            QiniuUtils.deleteFileFromQiniu(img_db);
            // 删除Redis中key值是SETMEAL_PIC_RESOURCE的图片名称
            jedisPool.getResource().srem(RedisPicConstant.SETMEAL_PIC_RESOURCE,img_db);
            // 删除Redis中key值是SETMEAL_PIC_DB_RESOURCE的图片名称
            jedisPool.getResource().srem(RedisPicConstant.SETMEAL_PIC_DB_RESOURCE,img_db);
        }
        // 再使用id，删除套餐
        setmealDao.delete(id);

        // 生成静态页面（通过WEB-INF/ftl/下的flt文件）（输出到healthmobile_web下的webapp/pages下）
        this.generateMobileStaticHtml();
    }

    @Override
    public List<Setmeal> findAll() {
        List<Setmeal> list = setmealDao.findAll();
        return list;
    }

    @Override
    public List<Map> findOrderCountBySetmealName() {
        List<Map> list = setmealDao.findOrderCountBySetmealName();
        return list;
    }

    // 建立套餐和检查组的关联关系，向中间表保存数据
    private void setSetmealAndCheckGruop(Integer setmealId, Integer[] checkgroupIds) {
        if(checkgroupIds!=null && checkgroupIds.length>0){
            for (Integer checkGroupId : checkgroupIds) {
                // 使用Map
                Map map = new HashMap();
                map.put("setmealId",setmealId);
                map.put("checkGroupId",checkGroupId);
                setmealDao.addSetmealAndCheckGroup(map);
            }
        }
    }
}
