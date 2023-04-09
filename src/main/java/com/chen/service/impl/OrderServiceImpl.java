package com.chen.service.impl;

import com.chen.common.ReturnType;
import com.chen.mapper.AccountMapper;
import com.chen.mapper.UserMapper;
import com.chen.pojo.Account;
import com.chen.pojo.Order;
import com.chen.mapper.OrderMapper;
import com.chen.pojo.SystemMessage;
import com.chen.pojo.User;
import com.chen.rabbitMQ.DelayQueueConfig;
import com.chen.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.socketio.ClientCache;
import com.chen.socketio.SystemMessageSender;
import com.chen.util.DecimalUtils;
import com.chen.util.ProfitUtil;
import com.chen.util.SnowFlakeUtil;
import com.chen.util.UserGetter;
import com.qiniu.rtc.model.MergeParam;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import javax.jws.soap.SOAPBinding;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Frozen
 * @since 2023-03-22
 */

@Service
@Transactional(rollbackFor = Exception.class)       /* 当程序发生异常时回滚 */
public class OrderServiceImpl implements OrderService {

    @Resource
    private AccountMapper accountMapper;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private SystemMessageSender systemMessageSender;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserGetter userGetter;

    @Override
    public ReturnType agree(Long accountId, Long buyerId, BigDecimal bid) {
        try {
            Account account = accountMapper.selectById(accountId);
            Long sellerId = userGetter.getUserId();
            // 防止重复出售
        /*if (account.getBought() == 1) {
            return new ReturnType().error("该账号已经出售");
        }*/
            // 修改bought值(下架)
            account.setBought(1);
            accountMapper.updateById(account);
            // 将订单存入订单表中
            Order order = new Order();
            order.setOrderId(SnowFlakeUtil.getSnowFlakeId());
            order.setBuyerPrice(bid);
            order.setAccountId(accountId);
            order.setBuyerId(buyerId);
            order.setSellerId(sellerId);
            orderMapper.insert(order);
            // 给买家发送付款消息
            Map<String,Object> data = new HashMap<>();
            data.put("account",account);
            data.put("orderId",order.getOrderId());
            SystemMessage systemMessage = new SystemMessage(ClientCache.PAY_EVENT,data,new Date());
            systemMessageSender.sendMsgById(buyerId,systemMessage);
            // 将取消订单消息加入消息队列
            rabbitTemplate.convertAndSend(DelayQueueConfig.NORMAL_EXCHANGE,DelayQueueConfig.CANCEL_ORDER_DELAY_ROUTING_KEY,order);
            return new ReturnType().success();
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); // 手动回滚
            return new ReturnType().error();
        }
    }

    @Override
    public ReturnType payment(Long orderId) {
        try {
            Order order = orderMapper.selectById(orderId);
            BigDecimal bid = order.getBuyerPrice();
            Long accountId = order.getAccountId();
            Long buyerId = order.getBuyerId();
            Account account = accountMapper.selectById(accountId);
            User user = userMapper.selectById(buyerId);
            BigDecimal balance = user.getBalance();
            // 防止重复付款
            if(order.getPaid() == 1) {
                return new ReturnType().error("请勿重复付款");
            }
            // 买家余额减少
            balance = DecimalUtils.subtract(balance,bid);
            user.setBalance(balance);
            userMapper.updateById(user);
            // 已付款
            order.setPaid(1);
            orderMapper.updateById(order);
            // 验货消息加入延时队列
            rabbitTemplate.convertAndSend(DelayQueueConfig.NORMAL_EXCHANGE,DelayQueueConfig.CHECK_ORDER_DELAY_ROUTING_KEY,order);
            // 给买家发送验货消息
            Map<String,Object> map = new HashMap<>();
            map.put("account",account);
            SystemMessage systemMessage = new SystemMessage(ClientCache.CHECK_EVENT,map,new Date());
            systemMessageSender.sendMsgById(buyerId,systemMessage);
            return new ReturnType().success();
        } catch (Exception e) {
            // 事务回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ReturnType().error();
        }
    }

    @Override
    public ReturnType noPayment(Long orderId) {
        try {
            // 删除订单
            orderMapper.deleteById(orderId);
            return new ReturnType().success();
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ReturnType().error();
        }
    }

    @Override
    public ReturnType checkOrder(Long orderId) {
        try {
            Order order = orderMapper.selectById(orderId);
            Long sellerId = order.getSellerId();
            User user = userMapper.selectById(sellerId);
            // 防止重复确认
            if (order.getChecked() == 1) {
                return new ReturnType().error("请勿重复确认");
            }
            // 修改订单验货状态
            order.setChecked(1);
            order.setFinished(1);
            orderMapper.updateById(order);
            // 卖家余额增长
            BigDecimal finalAmount = ProfitUtil.getFinalProfit(user.getBalance(),order.getBuyerPrice());
            user.setBalance(finalAmount);
            userMapper.updateById(user);
            return new ReturnType().success();
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ReturnType().error();
        }
    }
}
