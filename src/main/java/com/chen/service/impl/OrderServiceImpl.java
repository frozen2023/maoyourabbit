package com.chen.service.impl;

import com.chen.common.ReturnType;
import com.chen.mapper.AccountMapper;
import com.chen.mapper.ProblemMapper;
import com.chen.mapper.UserMapper;
import com.chen.pojo.*;
import com.chen.mapper.OrderMapper;
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
import javafx.beans.value.ObservableObjectValue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import javax.jws.soap.SOAPBinding;
import javax.management.ObjectName;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
    @Resource
    private ClientCache clientCache;
    @Resource
    private ProblemMapper problemMapper;

    @Override
    public ReturnType agree(Long accountId, Long buyerId, BigDecimal bid) {
        try {
            /*
            * 判断买家是否在线
            * 只有在买家上线的情况下才能进行下一步操作
            * */
            if(!clientCache.isOnline(buyerId)) {
                return new ReturnType().error("用户已下线");
            }
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
            SystemMessage systemMessage = SystemMessage.create(ClientCache.PAY_EVENT,data);
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
            // 用户掉线,10分钟后订单自动取消
            if (Objects.isNull(order)) {
                return new ReturnType().error("订单已取消");
            }
            BigDecimal bid = order.getBuyerPrice();
            Long accountId = order.getAccountId();
            Long buyerId = order.getBuyerId();
            Account account = accountMapper.selectById(accountId);
            User user = userMapper.selectById(buyerId);
            BigDecimal balance = user.getBalance();
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
            map.put("orderId",orderId);
            SystemMessage systemMessage = SystemMessage.create(ClientCache.CHECK_EVENT,map);
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
            Order order = orderMapper.selectById(orderId);
            if (Objects.isNull(order)) {
                return new ReturnType().error("订单已取消");
            }
            cancelOrderImpl(order);
            return new ReturnType().success();
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ReturnType().error();
        }
    }

    @Override
    public void cancelOrderImpl(Order order) {
        Long orderId = order.getOrderId();
        Long accountId = order.getAccountId();
        // 账号上架
        Account account = new Account();
        account.setAccountId(accountId);
        account.setBought(0);
        accountMapper.updateById(account);
        // 删除订单
        orderMapper.deleteById(orderId);
    }

    @Override
    public ReturnType checkOrder(Long orderId) {
        try {
            Order order = orderMapper.selectById(orderId);
            // 订单已经自动确认
            if (order.getChecked() == 1) {
                return new ReturnType().error("订单已自动确认");
            }
            checkOrderImpl(order);
            return new ReturnType().success();
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ReturnType().error();
        }
    }

    @Override
    public void checkOrderImpl(Order order) {
        Long sellerId = order.getSellerId();
        User user = userMapper.selectById(sellerId);
        // 修改订单验货状态
        order.setChecked(1);
        order.setFinished(1);
        orderMapper.updateById(order);
        // 卖家余额增长
        BigDecimal finalAmount = ProfitUtil.getFinalProfit(user.getBalance(),order.getBuyerPrice());
        user.setBalance(finalAmount);
        userMapper.updateById(user);
    }

    @Override
    public ReturnType uncheckOrder(Long orderId) {
        try {
            Order order = orderMapper.selectById(orderId);
            // 订单已经自动确认
            if (order.getChecked() == 1) {
                return new ReturnType().error("订单已自动确认");
            }
            Long accountId = order.getAccountId();
            Account account = accountMapper.selectById(accountId);
            Long sellerId = order.getSellerId();
            // 更改处理状态
            order.setChecked(1);
            orderMapper.updateById(order);
            // 将取消交易消息加入延时队列
            rabbitTemplate.convertAndSend(DelayQueueConfig.NORMAL_EXCHANGE,DelayQueueConfig.CANCEL_TRANSACTION_DELAY_ROUTING_KEY,order);
            // 给卖家发送取消交易系统消息
            Map<String,Object> data = new HashMap<>();
            data.put("orderId",orderId);
            data.put("account",account);
            SystemMessage systemMessage = SystemMessage.create(ClientCache.CANCEL_TRANSACTION_EVENT,data);
            systemMessageSender.sendMsgById(sellerId,systemMessage);
            return new ReturnType().success();
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ReturnType().error();
        }
    }

    @Override
    public ReturnType cancelTransaction(Long orderId) {
        try {
            Order order = orderMapper.selectById(orderId);
            if (Objects.isNull(order)) {
                return new ReturnType().error("订单已取消");
            }
            cancelTransactionImpl(order);
            return new ReturnType().success();
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ReturnType().error();
        }
    }

    @Override
    public void cancelTransactionImpl(Order order) {
        Long orderId = order.getOrderId();
        // 取消订单
        orderMapper.deleteById(orderId);
        // 账号解冻
        Long accountId = order.getAccountId();
        Account account = new Account();
        account.setAccountId(accountId);
        account.setBought(0);
        accountMapper.updateById(account);
        // 返还余额
        Long userId = order.getBuyerId();
        BigDecimal bid = order.getBuyerPrice();
        User user = userMapper.selectById(userId);
        BigDecimal balance = user.getBalance();
        balance = DecimalUtils.add(balance,bid);
        user.setBalance(balance);
        userMapper.updateById(user);
    }

    @Override
    public ReturnType rejectCancelTransaction(Long orderId, String detail) {
        try {
            Long userId = userGetter.getUserId();
            Problem problem = new Problem();
            problem.setProblemId(SnowFlakeUtil.getSnowFlakeId());
            problem.setOrderId(orderId);
            problem.setDetail(detail);
            problemMapper.insert(problem);
            System.out.println("id为" + userId + "的用户提交了一个账号问题");
            return new ReturnType().success();
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ReturnType().error();
        }
    }
}
