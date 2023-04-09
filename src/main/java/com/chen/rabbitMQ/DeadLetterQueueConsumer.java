package com.chen.rabbitMQ;

import com.chen.mapper.OrderMapper;
import com.chen.mapper.UserMapper;
import com.chen.pojo.Order;
import com.chen.pojo.User;
import com.chen.util.ProfitUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Objects;

@Component
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class DeadLetterQueueConsumer {

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private UserMapper userMapper;

    /*@Payload 和 @Headers注解可以消息中的 body 与 headers 信息*/

    /*
    * 监听取消订单消息，判断当订单未付款时取消订单
    * */
    @RabbitListener(queues = DelayQueueConfig.CANCEL_ORDER_DEAD_QUEUE)
    public void autoCancelOrder(@Payload Order order) {
        System.out.println("接收到来自取消订单延时队列的消息(账号)：" + order);
        Long orderId = order.getOrderId();
        // 查询最新付款状态
        Order latestOrder = orderMapper.selectById(orderId);
        if (!Objects.isNull(latestOrder) && latestOrder.getPaid() == 0) {
            orderMapper.deleteById(orderId);
            System.out.println("订单号为" + orderId + "的订单已自动取消");
        }
    }

    /*
    * 监听自动确认消息，判断当订单未确认时自动确认
    * */
    @RabbitListener(queues = DelayQueueConfig.CHECK_ORDER_DEAD_QUEUE)
    public void autoCheckOrder(@Payload Order order) {
        System.out.println("接收到来自自动确认延时队列的消息：" + order);
        Long orderId = order.getOrderId();
        Long sellerId = order.getSellerId();
        User user = userMapper.selectById(sellerId);
        Order latestOrder = orderMapper.selectById(orderId);
        // 订单未确认
        if(!Objects.isNull(latestOrder) && latestOrder.getChecked() == 0) {
            // 订单确认且已完成
            latestOrder.setChecked(1);
            latestOrder.setFinished(1);
            orderMapper.updateById(latestOrder);
            // 卖家余额增长
            BigDecimal finalAmount = ProfitUtil.getFinalProfit(user.getBalance(),latestOrder.getBuyerPrice());
            user.setBalance(finalAmount);
            userMapper.updateById(user);
        }
    }

}
