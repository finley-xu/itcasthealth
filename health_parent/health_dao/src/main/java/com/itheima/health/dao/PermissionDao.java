package com.itheima.health.dao;

import com.itheima.health.pojo.Permission;

import java.util.Set;

/**
 * @ClassName PermissionDao
 * @Description TODO
 * @Author ly
 * @Company 深圳黑马程序员
 * @Date 2020/2/25 14:22
 * @Version V1.0
 */
public interface PermissionDao {

    Set<Permission> findPermissionsByRoleId(Integer roleId);
}
