package com.itheima.health.service;

import com.itheima.health.pojo.Member;

import java.util.List;

public interface MemberService {

    Member findMemberByTelephone(String telepone);

    void add(Member member);

    List<Integer> findCountByBeforeRegTime(List<String> months);
}
