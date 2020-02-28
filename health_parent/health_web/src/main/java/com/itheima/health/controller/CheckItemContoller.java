package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.constant.MessageConstant;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.CheckItem;
import com.itheima.health.service.CheckItemService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName CheckItemContoller
 * @Description TODO
 * @Author ly
 * @Company 深圳黑马程序员
 * @Date 2020/2/13 16:04
 * @Version V1.0
 */
@RestController
@RequestMapping(value = "/checkitem")
public class CheckItemContoller {

    @Reference// 订阅 dubbo注解
    CheckItemService checkItemService;

    // 查询所有
    @RequestMapping(value = "/findAll")
    public Result findAll(){
        List<CheckItem> list = checkItemService.findAll();
        if(list!=null && list.size()>0){
            return new Result(true, MessageConstant.QUERY_CHECKITEM_SUCCESS,list);
        }else{
            return new Result(false, MessageConstant.QUERY_CHECKGROUP_FAIL);
        }
    }

    // 新增
    @RequestMapping(value = "/add")
    @PreAuthorize(value = "hasAuthority('CHECKITEM_ADD')")
    public Result add(@RequestBody CheckItem checkItem){
        try {
            checkItemService.add(checkItem);
            return new Result(true, MessageConstant.ADD_CHECKITEM_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.ADD_CHECKITEM_FAIL);
        }
    }

    // 分页查询
    @RequestMapping(value = "/findPage")
    @PreAuthorize(value = "hasAuthority('CHECKITEM_QUERY')")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){
        PageResult pageResult = checkItemService.findPage(queryPageBean.getCurrentPage(),
                                    queryPageBean.getPageSize(),
                                    queryPageBean.getQueryString());
        return pageResult;
    }

    // 删除检查项
    @RequestMapping(value = "/delete")
    @PreAuthorize(value = "hasAuthority('CHECKITEM_DELETE123')")
    public Result delete(Integer id){
        try {
            checkItemService.delete(id);
            return new Result(true, MessageConstant.DELETE_CHECKITEM_SUCCESS);
        } catch (RuntimeException re){
            re.printStackTrace();
            return new Result(false, re.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.DELETE_CHECKITEM_FAIL);
        }
    }

    // 检查项的ID查询
    @RequestMapping(value = "/findById")
    public Result findById(Integer id){
        CheckItem checkItem = checkItemService.findById(id);
        if(checkItem!=null){
            return new Result(true, MessageConstant.EDIT_CHECKITEM_SUCCESS,checkItem);
        }else{
            return new Result(false, MessageConstant.EDIT_CHECKITEM_FAIL);
        }
    }

    // 编辑保存检查项
    @RequestMapping(value = "/edit")
    @PreAuthorize(value = "hasAuthority('CHECKITEM_EDIT')")
    public Result edit(@RequestBody CheckItem checkItem){
        try {
            checkItemService.edit(checkItem);
            return new Result(true, MessageConstant.EDIT_CHECKITEM_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.EDIT_CHECKITEM_FAIL);
        }
    }
}
