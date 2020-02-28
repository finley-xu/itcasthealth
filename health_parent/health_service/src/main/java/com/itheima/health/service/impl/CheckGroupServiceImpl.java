package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.dao.CheckGroupDao;
import com.itheima.health.entity.PageResult;
import com.itheima.health.pojo.CheckGroup;
import com.itheima.health.service.CheckGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName CheckGroupServiceImpl
 * @Description TODO
 * @Author ly
 * @Company 深圳黑马程序员
 * @Date 2020/2/13 16:03
 * @Version V1.0
 */
@Service // dubbo提供
@Transactional
public class CheckGroupServiceImpl implements CheckGroupService {

    @Autowired
    CheckGroupDao checkGroupDao;

    @Override
    public void add(CheckGroup checkGroup, Integer[] checkitemIds) {
        // 第一步：接收到检查组的对象CheckGroup，执行保存检查组，保存检查组的同时，返回检查组ID
        checkGroupDao.add(checkGroup);
        // 第二步：接收到Integer类型的数组，存放检查项的id，和保存返回的检查组id，向中间表中插入数据
        this.setCheckGroupAndCheckItem(checkGroup.getId(),checkitemIds);

    }

    @Override
    public PageResult findPage(Integer currentPage, Integer pageSize, String queryString) {
        // 1：初始化分页
        PageHelper.startPage(currentPage,pageSize);
        // 2：查询
        Page<CheckGroup> page = checkGroupDao.findPage(queryString);
        // 3：封装结果集
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public CheckGroup findById(Integer id) {
        CheckGroup checkGroup = checkGroupDao.findById(id);
        return checkGroup;
    }

    @Override
    public List<Integer> findCheckItemIdsByCheckGroupId(Integer id) {
        List<Integer> list = checkGroupDao.findCheckItemIdsByCheckGroupId(id);
        return list;
    }

    @Override
    public void edit(CheckGroup checkGroup, Integer[] checkitemIds) {
        // 1：修改编辑检查组的相关信息字段，更新t_checkgroup
        checkGroupDao.edit(checkGroup);
        // 2：修改检查组和检查项的中间表，更新t_checkgroup_checkitem（中间表）
        // （1）使用检查组的id，先删除之前的数据
        checkGroupDao.deleteCheckGroupAndCheckItemByCheckGroupId(checkGroup.getId());
        // （2）重新建立检查组和检查项的关联关系，新增中间表的数据
        this.setCheckGroupAndCheckItem(checkGroup.getId(),checkitemIds);
    }

    @Override
    public void delete(Integer id) {
        // 删除检查组之前，判断检查组和检查项之间是否存在关联关系，如果存在关联关系不能删除
        Long count1 = checkGroupDao.findCheckGroupAndCheckItemByCheckGroupId(id);
        // 存在数据
        if(count1>0){
            throw new RuntimeException(MessageConstant.GET_CHECKGROUPANDCHECKITEMERROR);
        }
        // 删除检查组之前，判断检查组和套餐之间是否存在关联关系，如果存在关联关系不能删除
        Long count2 = checkGroupDao.findCheckGroupAndSetmealByCheckGroupId(id);
        // 存在数据
        if(count2>0){
            throw new RuntimeException(MessageConstant.GET_CHECKGROUPANDSETMEALERROR);
        }
        checkGroupDao.delete(id);
    }

    @Override
    public List<CheckGroup> findAll() {
        List<CheckGroup> list = checkGroupDao.findAll();
        return list;
    }

    // 向检查组和检查项的中间表中插入数据
    private void setCheckGroupAndCheckItem(Integer checkGroupId, Integer[] checkItemIds) {
        // 遍历
        if(checkItemIds!=null && checkItemIds.length>0){
            for (Integer checkItemId : checkItemIds) {
                // 保存（传递多个参数，在Dao的方法中，通过@Param指定方法参数的名称，例如：@Param(value = "checkGroupId") Integer checkGroupId, @Param(value = "checkItemId") Integer checkItemId）
                // checkGroupDao.addCheckGroupAndCheckItem(checkGroupId,checkItemId);
                // 传递Map（等同于JavaBean）
                Map map = new HashMap();
                map.put("checkGroupId",checkGroupId);
                map.put("checkItemId",checkItemId);
                checkGroupDao.addCheckGroupAndCheckItem(map);
            }
        }
    }
}
