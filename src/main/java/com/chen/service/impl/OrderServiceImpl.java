package com.chen.service.impl;

import com.chen.common.EventMsgs;
import com.chen.common.OrderStatus;
import com.chen.common.ReturnType;
import com.chen.mapper.AccountMapper;
import com.chen.mapper.ProblemMapper;
import com.chen.mapper.UserMapper;
import com.chen.pojo.*;
import com.chen.rabbitMQ.DelayQueueConfig;
import com.chen.repository.OrderRepository;
import com.chen.service.OrderService;
import com.chen.util.*;
import javafx.scene.control.Alert;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.*;
import org.springframework.data.redis.connection.SortParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

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
    private RabbitTemplate rabbitTemplate;
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserGetter userGetter;
    @Resource
    private ProblemMapper problemMapper;
    @Resource
    private OrderRepository orderRepository;

    @Override
    public ReturnType buyNow(Long accountId) {
        Order order = new Order();
        Long orderId = order.getOrderId();
        Account account = accountMapper.selectById(accountId);
        if (account.getBought() == 1)
            return new ReturnType().error("账号已被购买");
        Long sellerId = account.getSellerId();
        Long buyerId = userGetter.getUserId();
        BigDecimal bid = account.getPrice();
        try {
            // 生成订单
            List<OrderEvent> orderEvents = new ArrayList<>();
            orderEvents.add(new OrderEvent(EventMsgs.BUYER_GET));
            order.setAccountId(accountId);
            order.setOrderEvents(orderEvents);
            order.setStatus(OrderStatus.AWAITING_PAY);
            order.setSellerId(sellerId);
            order.setBuyerId(buyerId);
            order.setBid(bid);
            orderRepository.save(order);
            // 下架账号
            account.setBought(1);
            accountMapper.updateById(account);
            // 取消订单消息加入延时队列
            rabbitTemplate.convertAndSend(DelayQueueConfig.NORMAL_EXCHANGE,DelayQueueConfig.CANCEL_ORDER_DELAY_ROUTING_KEY,orderId);
            Map<String,Object> map = new HashMap<>();
            map.put("order",order);
            return new ReturnType().success(map);
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            orderRepository.deleteById(orderId);
            return new ReturnType().error();
        }
    }

    @Override
    public ReturnType pay(Long orderId) {
        Order order = orderRepository.findById(orderId).get();
        // 备份最初的状态
        Order preOrder = CloneUtil.deepClone(order);
        try {
            Long buyerId = order.getBuyerId();
            BigDecimal bid = order.getBid();
            User buyer = userMapper.selectById(buyerId);
            BigDecimal balance = buyer.getBalance();
            Long accountId = order.getAccountId();
            Account account = accountMapper.selectById(accountId);
            // 是否自动取消了
            if(order.getPaid() == 1) {
                return new ReturnType().error();
            }
            // 是否被冻结
            if(buyer.getFrozen() == 1) {
                return new ReturnType().error("用户钱包已被冻结");
            }
            // 余额是否充足
            if(balance.compareTo(bid) == -1) {
                return new ReturnType().error("余额不足");
            }
            // 买家余额减少
            balance = DecimalUtils.subtract(balance,bid);
            buyer.setBalance(balance);
            userMapper.updateById(buyer);
            // 修改订单状态
            order.setPaid(1);
            order.setStatus(OrderStatus.AWAITING_CHECK);
            List<OrderEvent> orderEvents = order.getOrderEvents();
            orderEvents.add(new OrderEvent(EventMsgs.BUYER_PAY));
            orderEvents.add(new OrderEvent(account));
            orderRepository.save(order);
            // 自动确认消息加入延时队列
            rabbitTemplate.convertAndSend(DelayQueueConfig.NORMAL_EXCHANGE,DelayQueueConfig.CHECK_ORDER_DELAY_ROUTING_KEY,orderId);
            Map<String,Object> map = new HashMap<>();
            map.put("order",order);
            return new ReturnType().success(map);
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            orderRepository.save(preOrder);
            return new ReturnType().error();
        }

    }

    @Override
    public ReturnType cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).get();
        Order preOrder = CloneUtil.deepClone(order);
        try {
            // 是否自动取消了
            if(order.getPaid() == 1) {
                return new ReturnType().error();
            }
            // 修改订单状态
            order.setPaid(1);
            order.setStatus(OrderStatus.CANCEL);
            List<OrderEvent> orderEvents = order.getOrderEvents();
            orderEvents.add(new OrderEvent(EventMsgs.BUYER_CANCEL_ORDER));
            orderRepository.save(order);
            Map<String,Object> map = new HashMap<>();
            map.put("order",order);
            return new ReturnType().success(map);
        } catch (Exception e) {
            e.printStackTrace();
            orderRepository.save(preOrder);
            return new ReturnType().error();
        }
    }

    @Override
    public ReturnType checkOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).get();
        Order preOrder = CloneUtil.deepClone(order);
        try {
            // 是否已自动确认
            if(order.getChecked() == 1) {
                return new ReturnType().error();
            }
            // 修改订单状态
            order.setChecked(1);
            order.setFinished(1);
            order.setStatus(OrderStatus.FINISH);
            List<OrderEvent> orderEvents = order.getOrderEvents();
            orderEvents.add(new OrderEvent(EventMsgs.BUYER_CHECK));
            orderRepository.save(order);
            // 卖家余额增长
            Long sellerId = order.getSellerId();
            User seller = userMapper.selectById(sellerId);
            BigDecimal balance = seller.getBalance();
            BigDecimal bid = order.getBid();
            BigDecimal finalProfit = ProfitUtil.getFinalProfit(balance, bid);
            seller.setBalance(finalProfit);
            userMapper.updateById(seller);
            Map<String,Object> map = new HashMap<>();
            map.put("order",order);
            return new ReturnType().success(map);
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            orderRepository.save(preOrder);
            return new ReturnType().error();
        }
    }

    @Override
    public ReturnType rejectCheck(Long orderId) {
        Order order = orderRepository.findById(orderId).get();
        Order preOrder = CloneUtil.deepClone(order);
        try {
            // 是否已自动确认
            if(order.getChecked() == 1) {
                return new ReturnType().error();
            }
            // 更改订单状态
            order.setChecked(1);
            order.setStatus(OrderStatus.AWAITING_INSPECT);
            List<OrderEvent> orderEvents = order.getOrderEvents();
            orderEvents.add(new OrderEvent(EventMsgs.BUYER_CHECK_REJECT));
            orderRepository.save(order);
            // 取消交易消息加入延时队列
            rabbitTemplate.convertAndSend(DelayQueueConfig.NORMAL_EXCHANGE,DelayQueueConfig.CANCEL_TRANSACTION_DELAY_ROUTING_KEY,orderId);
            Map<String,Object> map = new HashMap<>();
            map.put("order",order);
            return new ReturnType().success(map);
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            orderRepository.save(preOrder);
            return new ReturnType().error();
        }
    }

    @Override
    public ReturnType cancelTransaction(Long orderId) {
        Order order = orderRepository.findById(orderId).get();
        Order preOrder = CloneUtil.deepClone(order);
        try {
            // 是否已自动操作
            if(order.getCanceled() == 1) {
                return new ReturnType().error();
            }
            // 修改订单状态
            order.setCanceled(1);
            order.setStatus(OrderStatus.CANCEL);
            List<OrderEvent> orderEvents = order.getOrderEvents();
            orderEvents.add(new OrderEvent(EventMsgs.SELLER_CANCEL_TRANSACTION));
            orderRepository.save(order);
            // 账号解冻
            Long accountId = order.getAccountId();
            Account account = new Account();
            account.setAccountId(accountId);
            account.setBought(0);
            accountMapper.updateById(account);
            // 返还余额
            Long userId = order.getBuyerId();
            BigDecimal bid = order.getBid();
            User user = userMapper.selectById(userId);
            BigDecimal balance = user.getBalance();
            balance = DecimalUtils.add(balance,bid);
            user.setBalance(balance);
            userMapper.updateById(user);
            Map<String,Object> map = new HashMap<>();
            map.put("order",order);
            return new ReturnType().success(map);
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            orderRepository.save(preOrder);
            return new ReturnType().error();
        }
    }

    @Override
    public ReturnType rejectCancelTransaction(Long orderId, String detail) {
        Order order = orderRepository.findById(orderId).get();
        Order preOrder = CloneUtil.deepClone(order);
        try {
            // 是否已自动操作
            if(order.getCanceled() == 1) {
                return new ReturnType().error();
            }
            // 修改订单状态
            order.setCanceled(1);
            order.setStatus(OrderStatus.AWAITING_DEAL);
            List<OrderEvent> orderEvents = order.getOrderEvents();
            orderEvents.add(new OrderEvent(EventMsgs.SELLER_CANCEL_TRANSACTION_REJECT));
            orderRepository.save(order);
            // 提交问题至管理员
            Long userId = userGetter.getUserId();
            Problem problem = new Problem();
            problem.setProblemId(SnowFlakeUtil.getSnowFlakeId());
            problem.setOrderId(orderId);
            problem.setDetail(detail);
            problemMapper.insert(problem);
            System.out.println("id为" + userId + "的用户提交了一个账号问题");
            Map<String,Object> map = new HashMap<>();
            map.put("order",order);
            return new ReturnType().success(map);
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            orderRepository.save(preOrder);
            return new ReturnType().error();
        }
    }

    @Override
    public ReturnType getOrders(Integer type, Integer finished, Integer page) {
        // 按时间排序并分页
        Sort sort = Sort.by(Sort.Direction.DESC, "mgtCreate");
        if (page == 0)
            return new ReturnType().error();
        page = page - 1;
        Pageable pageable = PageRequest.of(page,5,sort);
        Long userId = userGetter.getUserId();
        Map<String,Object> data = new HashMap<>();
        Page<Order> orderPage = null;
        // 已购买
        if(type == 1) {
            orderPage = orderRepository.findAllByBuyerIdAndFinished(userId, finished, pageable);
        } else {
            // 已出售
            orderPage = orderRepository.findAllBySellerIdAndFinished(userId, finished, pageable);
        }
        int totalPages = orderPage.getTotalPages();
        List<Order> orders = orderPage.getContent();
        data.put("orders",orders);
        data.put("totalPages",totalPages);
        return new ReturnType().success(data);
    }

    @Override
    public ReturnType getOrderById(Long orderId) {
        Optional<Order> orderOp = orderRepository.findById(orderId);
        if(orderOp.isPresent()) {
            Order order = orderOp.get();
            Map<String,Object> data = new HashMap<>();
            data.put("order",order);
            return new ReturnType().success(data);
        }
        return new ReturnType().error("未找到该订单");
    }
}
