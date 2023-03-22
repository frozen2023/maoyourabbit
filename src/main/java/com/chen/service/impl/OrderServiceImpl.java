package com.chen.service.impl;

import com.chen.pojo.Order;
import com.chen.mapper.OrderMapper;
import com.chen.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Frozen
 * @since 2023-03-22
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

}
