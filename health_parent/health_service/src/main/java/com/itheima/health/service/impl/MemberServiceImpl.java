package com.itheima.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.health.dao.MemberDao;
import com.itheima.health.pojo.Member;
import com.itheima.health.service.MemberService;
import com.itheima.health.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName MemberServiceImpl
 * @Description TODO
 * @Author ly
 * @Company 深圳黑马程序员
 * @Date 2020/2/13 16:03
 * @Version V1.0
 */
@Service // dubbo提供
@Transactional
public class MemberServiceImpl implements MemberService {

    @Autowired
    MemberDao memberDao;

    @Override
    public Member findMemberByTelephone(String telepone) {
        Member member = memberDao.findMemberByTelephone(telepone);
        return member;
    }

    @Override
    public void add(Member member) {
        // 判断密码是否为空（密码为空：使用手机快速登录注册；密码不为空：使用用户登录注册的应用场景）
        if(member!=null && member.getPassword()!=null){
            member.setPassword(MD5Utils.md5(member.getPassword())); // 将明文的密码使用md5进行加密
        }
        memberDao.add(member);
    }

    @Override
    public List<Integer> findCountByBeforeRegTime(List<String> months) {
        List<Integer> memberCount  = new ArrayList<>();
        // 遍历
        for (String month : months) {
            // 方案一：使用Calader的日历类，计算每个月的最后1天，添加到后面（回去按照讲的方法，查）
            // 方案二：补-31
            String regTime = month+"-31";
            Integer count = memberDao.findCountByBeforeRegTime(regTime);
            memberCount.add(count);
        }
        return memberCount;
    }
}
