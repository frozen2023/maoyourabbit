package com.chen.rabbitMQ;

import com.chen.mapper.OrderMapper;
import com.chen.pojo.Order;
import com.chen.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.Objects;

@Component
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class DeadLetterQueueConsumer {

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderService orderService;

    /*@Payload 和 @Headers注解可以消息中的 body 与 headers 信息*/

    /*
    * 监听取消订单消息，判断当订单未付款时取消订单
    * */
    @RabbitListener(queues = DelayQueueConfig.CANCEL_ORDER_DEAD_QUEUE)
    public void autoCancelOrder(@Payload Order order) {
        System.out.println("接收到来自取消订单延时队列的消息：" + order);
        Long orderId = order.getOrderId();
        // 查询最新付款状态
        Order latestOrder = orderMapper.selectById(orderId);
        if (!Objects.isNull(latestOrder) && latestOrder.getPaid() == 0) {
            orderService.cancelOrderImpl(latestOrder);
            System.out.println("订单号为" + orderId + "的订单已自动取消");
        }
    }

    /*
    * 监听自动确认消息，判断当卖家未处理时自动确认
    * */
    @RabbitListener(queues = DelayQueueConfig.CHECK_ORDER_DEAD_QUEUE)
    public void autoCheckOrder(@Payload Order order) {
        System.out.println("接收到来自自动确认延时队列的消息：" + order);
        Long orderId = order.getOrderId();
        Order latestOrder = orderMapper.selectById(orderId);
        // 订单未确认
        if(!Objects.isNull(latestOrder) && latestOrder.getChecked() == 0) {
            orderService.checkOrderImpl(latestOrder);
            System.out.println("订单号为" + orderId + "的订单已自动确认");
        }
    }

    /*
    * 监听取消交易消息，当卖家未处理时自动取消订单并返还买家余额
    * */
    @RabbitListener(queues = DelayQueueConfig.CANCEL_TRANSACTION_DEAD_QUEUE)
    public void autoCancelTransaction(@Payload Order order) {
        System.out.println("接收到来取消交易延时队列的消息：" + order);
        Long orderId = order.getOrderId();
        Order latestOrder = orderMapper.selectById(orderId);
        //  判断未处理
        if (!Objects.isNull(latestOrder) && latestOrder.getCancled() == 0) {
            orderService.cancelTransactionImpl(latestOrder);
            System.out.println("订单号为" + orderId + "的订单已自动取消");
        }
    }

}
