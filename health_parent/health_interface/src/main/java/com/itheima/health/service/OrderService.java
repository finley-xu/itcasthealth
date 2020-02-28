package com.itheima.health.service;

import com.itheima.health.entity.Result;

import java.util.Map;

public interface OrderService {

    Result orderSubmit(Map map);

    Map findById(Integer id);
}
