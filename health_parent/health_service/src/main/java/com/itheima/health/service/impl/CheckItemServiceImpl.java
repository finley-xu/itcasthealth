package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.dao.CheckItemDao;
import com.itheima.health.entity.PageResult;
import com.itheima.health.pojo.CheckItem;
import com.itheima.health.service.CheckItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ClassName CheckItemServiceImpl
 * @Description TODO
 * @Author ly
 * @Company 深圳黑马程序员
 * @Date 2020/2/13 16:03
 * @Version V1.0
 */
@Service // dubbo提供
@Transactional
public class CheckItemServiceImpl implements CheckItemService {

    @Autowired
    CheckItemDao checkItemDao;

    @Override
    public List<CheckItem> findAll() {
        List<CheckItem> list = checkItemDao.findAll();
        return list;
    }

    @Override
    public void add(CheckItem checkItem) {
        checkItemDao.add(checkItem);
    }

    @Override
    public PageResult findPage(Integer currentPage, Integer pageSize, String queryString) {
        // 一：传统开发的做法，写sql语句完成分页（不好，运维不适合数据库的改变和迁移）
        // 1：查询总记录数，定义sql，select count(*) from t_checkitem where code = queryString or name = queryString
        // long total;
        // 2：使用查询条件查询当前页的数据（分页），定义sql，select * from t_checkitem where code = queryString or name = queryString limit ?,?
        // limit ?,?（第1个问号表示当前页从第几条开始检索，默认是0,0表示第1条，第2个问号表示当前页最多显示的记录数）
        //          (currentPage-1)*pageSize                                     pageSize
        // List<CheckItem> rows
        // 封装PageResult
        // return new PageResult(total,rows);
        /****************************************************************************************************/
        // 二：使用Mybatis的分页插件，不使用分页的查询，也能完成（即不使用limit），在applicationContext-dao.xml中定义分页插件
        // 1：初始化数据
        PageHelper.startPage(currentPage,pageSize);
        // 2：查询，第一种，返回Page
        // Page<CheckItem> page = checkItemDao.findPage(queryString);
        // 3：封装PageResult
        // return new PageResult(page.getTotal(),page.getResult());

        // 2：查询，第二种，返回List<CheckItem>
        List<CheckItem> list = checkItemDao.findPage(queryString);
        PageInfo<CheckItem> pageInfo = new PageInfo<CheckItem>(list);
        // 3：封装PageResult
        return new PageResult(pageInfo.getTotal(),pageInfo.getList());
    }

    @Override
    public void delete(Integer id) {
        // 1：在删除检查项之前，给予判断，判断当前检查项是否被检查组关联，如果没有关联，可以删除；如果关联，是不能删除，给予提示
        long count = checkItemDao.findCheckGroupAndCheckItemCountByCheckItemId(id);
        // 存在数据，此时不能删除
        if(count>0){
            // 给予提示（异常），将异常抛给Controller，只有运行时异常，spring的事务处理才能捕获
            throw new RuntimeException(MessageConstant.GET_CHECKITEMANDCHECKGROUPERROR);
        }
        // 不存在，删除检查项
        checkItemDao.delete(id);
    }

    @Override
    public CheckItem findById(Integer id) {
        return checkItemDao.findById(id);
    }

    @Override
    public void edit(CheckItem checkItem) {
        checkItemDao.edit(checkItem);
    }
}
