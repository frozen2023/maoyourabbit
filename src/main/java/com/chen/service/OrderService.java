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
    ReturnType agree(Long accountId, Long buyerId, BigDecimal bid);
    ReturnType payment(Long orderId);
    ReturnType noPayment(Long orderId);
    ReturnType checkOrder(Long orderId);
    ReturnType uncheckOrder(Long orderId);
    ReturnType cancelTransaction(Long orderId);
    ReturnType rejectCancelTransaction(Long orderId, String detail);
    void cancelTransactionImpl(Order order);
    void checkOrderImpl(Order order);
    void cancelOrderImpl(Order order);
}
