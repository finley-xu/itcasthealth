package com.itheima.health.dao;

import com.github.pagehelper.Page;
import com.itheima.health.pojo.Setmeal;

import java.util.List;
import java.util.Map;

public interface SetmealDao {

    void add(Setmeal setmeal);

    void addSetmealAndCheckGroup(Map map);

    Page<Setmeal> findPage(String queryString);

    Setmeal findById(Integer id);

    List<Integer> findCheckGroupIdsBySetmealId(Integer id);

    void edit(Setmeal setmeal);

    void deleteSetmealAndCheckGroupBySetmealId(Integer id);

    void delete(Integer id);

    Long findSetmealAndCheckGroupCountBySetmealId(Integer id);

    List<Setmeal> findAll();

    List<Map> findOrderCountBySetmealName();

}
