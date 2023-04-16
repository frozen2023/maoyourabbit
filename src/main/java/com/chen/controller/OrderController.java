package com.chen.controller;

import com.chen.common.ReturnType;
import com.chen.security.annotations.IsUser;
import com.chen.service.OrderService;
import com.chen.util.DecimalUtils;
import com.chen.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
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
    @PostMapping("/order/pay")
    public ReturnType payment(@RequestBody Map map) {
        Long orderId = ObjectUtils.toLong(map.get("orderId"));
        return orderService.payment(orderId);
    }

    // 买家拒绝付款
    @IsUser
    @PostMapping("/order/pay/reject")
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

    // 买家拒绝收货
    @IsUser
    @PostMapping("/order/check/reject")
    public ReturnType uncheckOrder(@RequestBody Map map) {
        Long orderId = ObjectUtils.toLong(map.get("orderId"));
        return orderService.uncheckOrder(orderId);
    }

    // 卖家主动取消交易
    @IsUser
    @PostMapping("/order/cancel")
    public ReturnType cancelTransaction(@RequestBody Map map) {
        Long orderId = ObjectUtils.toLong(map.get("orderId"));
        return orderService.cancelTransaction(orderId);
    }

    // 卖家拒绝取消
    @IsUser
    @PostMapping("/order/cancel/reject")
    public ReturnType rejectCancelTransaction(@RequestBody Map map) {
        Long orderId = ObjectUtils.toLong(map.get("orderId"));
        String detail = ObjectUtils.toString(map.get("detail"));
        return orderService.rejectCancelTransaction(orderId,detail);
    }


}
