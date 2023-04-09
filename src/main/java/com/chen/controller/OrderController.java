package com.chen.controller;

import com.chen.common.ReturnType;
import com.chen.security.annotations.IsUser;
import com.chen.service.OrderService;
import com.chen.util.DecimalUtils;
import com.chen.util.ObjectUtils;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.beans.BeanInfo;
import java.math.BigDecimal;
import java.util.Map;

/**
 * @author Frozen
 * @since 2023-03-22
 */
@RestController
public class OrderController {
    @Resource
    private OrderService orderService;

    // 卖家同意
    @IsUser
    @PostMapping("/order/agree")
    public ReturnType agree(@RequestBody Map map) {
        Long accountId = ObjectUtils.toLong(map.get("accountId"));
        Long buyerId = ObjectUtils.toLong(map.get("buyerId"));
        BigDecimal bid = DecimalUtils.toBigDecimal(map.get("bid"));
        return orderService.agree(accountId,buyerId,bid);
    }

    // 卖家付款
    @IsUser
    @PostMapping("/order/payment")
    public ReturnType payment(@RequestBody Map map) {
        Long orderId = ObjectUtils.toLong(map.get("orderId"));
        return orderService.payment(orderId);
    }

    // 买家拒绝付款
    @IsUser
    @PostMapping("/order/noPayment")
    public ReturnType noPayment(@RequestBody Map map) {
        Long orderId = ObjectUtils.toLong(map.get("orderId"));
        return orderService.noPayment(orderId);
    }

    // 买家确认收货
    @IsUser
    @PostMapping("/order/check")
    public ReturnType checkOrder(@RequestBody Map map) {
        Long orderId = ObjectUtils.toLong(map.get("orderId"));
        return orderService.checkOrder(orderId);
    }

}
