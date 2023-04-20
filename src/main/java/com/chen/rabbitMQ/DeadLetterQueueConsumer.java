package com.chen.rabbitMQ;

import com.chen.common.EventMsgs;
import com.chen.common.OrderStatus;
import com.chen.mapper.AccountMapper;
import com.chen.mapper.UserMapper;
import com.chen.pojo.*;
import com.chen.repository.OrderRepository;
import com.chen.repository.SystemMessageRepository;
import com.chen.socketio.SystemMessageSender;
import com.chen.util.DecimalUtils;
import com.chen.util.ProfitUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class DeadLetterQueueConsumer {

    @Resource
    private OrderRepository orderRepository;
    @Resource
    private UserMapper userMapper;
    @Resource
    private AccountMapper accountMapper;
    @Resource
    private SystemMessageSender systemMessageSender;
    @Resource
    private SystemMessageRepository systemMessageRepository;

    /*@Payload 和 @Headers注解可以消息中的 body 与 headers 信息


    * 监听取消订单消息，判断当订单未付款时取消订单
    * */
    @RabbitListener(queues = DelayQueueConfig.CANCEL_ORDER_DEAD_QUEUE)
    public void autoCancelOrder(@Payload Long orderId) {
        System.out.println("接收到来自取消订单延时队列的消息：" + orderId);
        // 查询最新付款状态
        Optional<Order> orderOp = orderRepository.findById(orderId);
        // 买家未操作
        if(orderOp.isPresent()) {
            Order latestOrder = orderOp.get();
            if(latestOrder.getPaid() == 0) {
                // 账号上架
                Long accountId = latestOrder.getAccountId();
                Account account = new Account();
                account.setAccountId(accountId);
                account.setBought(0);
                accountMapper.updateById(account);
                // 更改订单状态
                latestOrder.setPaid(1);
                latestOrder.setStatus(OrderStatus.CANCEL);
                List<OrderEvent> orderEvents = latestOrder.getOrderEvents();
                orderEvents.add(new OrderEvent(EventMsgs.BUYER_PAY_TIMEOUT));
                orderRepository.save(latestOrder);
                // 给买家发送提醒
                Long buyerId = latestOrder.getBuyerId();
                SystemMessage systemMessage = new SystemMessage();
                systemMessage.setData(latestOrder);
                systemMessage.setReceiverId(buyerId);
                systemMessage.setType(SystemMessage.AUTO_CANCEL);
                systemMessageSender.sendMsgById(buyerId,systemMessage);
                systemMessageRepository.save(systemMessage);
                System.out.println("订单号为" + orderId + "的订单已自动取消");
            }
        }
    }

    /*
    * 监听自动确认消息，判断当卖家未处理时自动确认
    **/
    @RabbitListener(queues = DelayQueueConfig.CHECK_ORDER_DEAD_QUEUE)
    public void autoCheckOrder(@Payload Long orderId) {
        System.out.println("接收到来自自动确认延时队列的消息：" + orderId);
        Optional<Order> orderOp = orderRepository.findById(orderId);
        // 买家未操作
        if(orderOp.isPresent()) {
            Order latestOrder = orderOp.get();
            if(latestOrder.getChecked() == 0) {
                // 卖家余额增长
                Long sellerId = latestOrder.getSellerId();
                User seller = userMapper.selectById(sellerId);
                BigDecimal balance = seller.getBalance();
                BigDecimal bid = latestOrder.getBid();
                BigDecimal finalProfit = ProfitUtil.getFinalProfit(balance, bid);
                seller.setBalance(finalProfit);
                userMapper.updateById(seller);
                // 修改订单状态
                latestOrder.setChecked(1);
                latestOrder.setFinished(1);
                latestOrder.setStatus(OrderStatus.FINISH);
                List<OrderEvent> orderEvents = latestOrder.getOrderEvents();
                orderEvents.add(new OrderEvent(EventMsgs.BUYER_CHECK_TIMEOUT));
                orderRepository.save(latestOrder);
                // 给买家发送提醒
                Long buyerId = latestOrder.getBuyerId();
                SystemMessage systemMessage = new SystemMessage();
                systemMessage.setData(latestOrder);
                systemMessage.setReceiverId(buyerId);
                systemMessage.setType(SystemMessage.AUTO_CHECK);
                systemMessageSender.sendMsgById(buyerId,systemMessage);
                systemMessageRepository.save(systemMessage);
                System.out.println("订单号为" + orderId + "的订单已自动确认");
            }
        }
    }
    /*
    * 监听取消交易消息，当卖家未处理时自动取消订单并返还买家余额
    **/
    @RabbitListener(queues = DelayQueueConfig.CANCEL_TRANSACTION_DEAD_QUEUE)
    public void autoCancelTransaction(@Payload Long orderId) {
        System.out.println("接收到来取消交易延时队列的消息：" + orderId);
        Optional<Order> orderOp = orderRepository.findById(orderId);
        // 卖家未操作
        if (orderOp.isPresent()) {
            Order latestOrder = orderOp.get();
            if (latestOrder.getCanceled() == 0) {
                // 账号解冻
                Long accountId = latestOrder.getAccountId();
                Account account = new Account();
                account.setAccountId(accountId);
                account.setBought(0);
                accountMapper.updateById(account);
                // 返还余额
                Long userId = latestOrder.getBuyerId();
                BigDecimal bid = latestOrder.getBid();
                User user = userMapper.selectById(userId);
                BigDecimal balance = user.getBalance();
                balance = DecimalUtils.add(balance,bid);
                user.setBalance(balance);
                userMapper.updateById(user);
                // 修改订单状态
                latestOrder.setCanceled(1);
                latestOrder.setStatus(OrderStatus.CANCEL);
                List<OrderEvent> orderEvents = latestOrder.getOrderEvents();
                orderEvents.add(new OrderEvent(EventMsgs.SELLER_INSPECT_TIMEOUT));
                orderRepository.save(latestOrder);
                // 给卖家发送提醒
                Long sellerId = latestOrder.getSellerId();
                SystemMessage systemMessage = new SystemMessage();
                systemMessage.setData(latestOrder);
                systemMessage.setReceiverId(sellerId);
                systemMessage.setType(SystemMessage.AUTO_CANCEL_TRANSACTION);
                systemMessageSender.sendMsgById(sellerId,systemMessage);
                systemMessageRepository.save(systemMessage);
                System.out.println("订单号为" + orderId + "的订单已自动确认");
            }
        }
    }
}
