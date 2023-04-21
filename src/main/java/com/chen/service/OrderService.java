package com.chen.service;

import com.chen.common.ReturnType;
import com.chen.pojo.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import sun.reflect.generics.tree.VoidDescriptor;

import java.math.BigDecimal;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Frozen
 * @since 2023-03-22
 */
public interface OrderService {
    ReturnType buyNow(Long accountId);
    ReturnType pay(Long orderId);
    ReturnType cancelOrder(Long orderId);
    ReturnType checkOrder(Long orderId);
    ReturnType rejectCheck(Long orderId);
    ReturnType cancelTransaction(Long orderId);
    ReturnType rejectCancelTransaction(Long orderId, String detail);
    ReturnType getOrders(Integer type, Integer finished, Integer page);
    ReturnType getOrderById(Long orderId);
}
