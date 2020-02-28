package com.itheima.health.dao;

import com.itheima.health.pojo.Member;

public interface MemberDao {

    Member findMemberByTelephone(String telepone);

    void add(Member member);

    Integer findCountByBeforeRegTime(String regTime);
}
