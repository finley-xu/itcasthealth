package com.itheima.health.security;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.pojo.Permission;
import com.itheima.health.pojo.Role;
import com.itheima.health.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName SpringSecurityUserService
 * @Description TODO
 * @Author ly
 * @Company 深圳黑马程序员
 * @Date 2020/2/25 10:13
 * @Version V1.0
 */
@Component // 组件，相当于spring的配置文件中定义<bean id="springSecurityUserService" class="com.itheima.health.security.SpringSecurityUserService">
public class SpringSecurityUserService implements UserDetailsService {

    @Reference
    UserService userService;

    /** 执行/login.do的url的时候，就会执行loadUserByUsername的方法
     * @param username：表示用户名，接收表单用户名的数据
     * @return UserDetails：封装的用户信息对象，用来存放用户名，密码，权限
     *                       等同于<security:user name="admin" password="{noop}123" authorities="ROLE_ADMIN"></security:user>
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("进行登录校验");
        // 1：使用用户名作为查询条件，查询用户信息（从数据库查询，此时User既有用户信息，也有角色的集合，同时也有权限的集合）
        com.itheima.health.pojo.User user = userService.findUserByUsername(username);
        // 此时说明用户名输入有误，返回null，如果UserDetails对象为null，抛出异常，回退到登录页面，表示登录名输入有误，org.springframework.security.authentication.InternalAuthenticationServiceException: UserDetailsService returned null
        if(user==null){
            return null;
        }
        String password = user.getPassword();
        // 2：对当前用户分配角色和权限
        List<GrantedAuthority> authorities = new ArrayList<>();
        if(user.getRoles()!=null && user.getRoles().size()>0){
            for (Role role : user.getRoles()) {
                authorities.add(new SimpleGrantedAuthority(role.getKeyword())); // 添加角色
                if(role.getPermissions()!=null && role.getPermissions().size()>0){
                    for (Permission permission : role.getPermissions()) {
                        authorities.add(new SimpleGrantedAuthority(permission.getKeyword()));         // 添加权限
                    }
                }
            }
        }
        // 3：组织封装UserDetails对象
        /**
         * User(String username, String password, Collection<? extends GrantedAuthority> authorities)
         * 其中参数2：表示从数据库查询的密码，对于SpringSecurity来说，自动使用该密码password和表单页面传递的密码进行比对
         *             如果一致，跳转到登录成功页面index.html；如果不一致，抛出异常，回退到登录页面login.html
         *
         *             org.springframework.security.authentication.BadCredentialsException: Bad credentials
         */
        return new User(username,password,authorities);
    }
}
