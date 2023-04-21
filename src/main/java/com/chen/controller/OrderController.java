package com.chen.controller;

import com.chen.common.ReturnType;
import com.chen.security.annotations.IsAdmin;
import com.chen.security.annotations.IsUser;
import com.chen.service.OrderService;
import com.chen.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author Frozen
 * @since 2023-03-22
 */
@RestController
public class OrderController {
    @Resource
    private OrderService orderService;

    // 立即购买
    @IsUser
    @PostMapping("/order/buy-now")
    public ReturnType buyNow(@RequestBody Map map) {
        Long accountId = ObjectUtils.toLong(map.get("accountId"));
        return orderService.buyNow(accountId);
    }

    // 买家付款
    @IsUser
    @PostMapping("/order/pay")
    public ReturnType pay(@RequestBody Map map) {
        Long orderId = ObjectUtils.toLong(map.get("orderId"));
        return orderService.pay(orderId);
    }

    // 买家取消订单
    @IsUser
    @PostMapping("/order/cancel")
    public ReturnType cancelOrder(@RequestBody Map map) {
        Long orderId = ObjectUtils.toLong(map.get("orderId"));
        return orderService.cancelOrder(orderId);
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
    public ReturnType rejectCheck(@RequestBody Map map) {
        Long orderId = ObjectUtils.toLong(map.get("orderId"));
        return orderService.rejectCheck(orderId);
    }

    // 卖家取消交易
    @IsUser
    @PostMapping("/order/cancelTransaction")
    public ReturnType cancelTransaction(@RequestBody Map map) {
        Long orderId = ObjectUtils.toLong(map.get("orderId"));
        return orderService.cancelTransaction(orderId);
    }

    // 卖家拒绝取消
    @IsUser
    @PostMapping("/order/cancelTransaction/reject")
    public ReturnType rejectCancelTransaction(@RequestBody Map map) {
        Long orderId = ObjectUtils.toLong(map.get("orderId"));
        String detail = ObjectUtils.toString(map.get("detail"));
        return orderService.rejectCancelTransaction(orderId,detail);
    }

    // 获取订单
    @IsUser
    @GetMapping("/order/{type}/{finished}/{page}")
    public ReturnType getOrders(@PathVariable("type") Integer type,
                                @PathVariable("finished") Integer finished,
                                @PathVariable("page") Integer page) {
        return orderService.getOrders(type,finished,page);
    }

    // 根据id查找订单
    @IsAdmin
    @GetMapping("/order/{orderId}")
    public ReturnType getOrderById(@PathVariable("orderId") Long orderId) {
        return orderService.getOrderById(orderId);
    }
}
