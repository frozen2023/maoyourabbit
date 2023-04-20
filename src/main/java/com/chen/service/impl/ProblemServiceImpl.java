package com.chen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.common.ReturnType;
import com.chen.mapper.UserMapper;
import com.chen.pojo.Order;
import com.chen.pojo.Problem;
import com.chen.mapper.ProblemMapper;
import com.chen.pojo.SystemMessage;
import com.chen.pojo.User;
import com.chen.repository.OrderRepository;
import com.chen.repository.SystemMessageRepository;
import com.chen.service.ProblemService;
import com.chen.service.ReportService;
import com.chen.socketio.SystemMessageSender;
import com.chen.util.DecimalUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Frozen
 * @since 2023-03-22
 */
@Service
public class ProblemServiceImpl implements ProblemService {
    @Resource
    private ProblemMapper problemMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private OrderRepository orderRepository;
    @Resource
    private SystemMessageSender systemMessageSender;
    @Resource
    private SystemMessageRepository systemMessageRepository;
    @Resource
    private ReportService reportService;
    
    @Override
    public ReturnType getProblems(Integer page, Integer solved) {
        Page<Problem> problemPage = new Page<>(page, 5);
        QueryWrapper<Problem> wrapper = new QueryWrapper<>();
        wrapper.eq("solved",solved);
        Page<Problem> selectPage = problemMapper.selectPage(problemPage, wrapper);
        long pages = selectPage.getPages();
        List<Problem> problems = selectPage.getRecords();
        Map<String,Object> data = new HashMap<>();
        data.put("pages",pages);
        data.put("problems",problems);
        return new ReturnType().success(data);
    }

    @Override
    public ReturnType handleProblem(Long problemId, Double level) {
        try {
            Problem problem = problemMapper.selectById(problemId);
            if(problem.getSolved() == 1) {
                return new ReturnType().error();
            }
            Long orderId = problem.getOrderId();
            Order order = orderRepository.findById(orderId).get();
            BigDecimal bid = order.getBid();
            Long sellerId = order.getSellerId();
            User seller = userMapper.selectById(sellerId);
            Long buyerId = order.getBuyerId();
            User buyer = userMapper.selectById(buyerId);
            BigDecimal sellerBalance = seller.getBalance();
            BigDecimal buyerBalance = buyer.getBalance();
            if(Double.compare(level,1.00) == 0) {// 账号严重受损
                // 资金全部转卖家
                seller.setBalance(DecimalUtils.add(sellerBalance,bid));
                // 买家加入黑名单
                reportService.toBlackList(buyer);
                userMapper.updateById(seller);
                userMapper.updateById(buyer);
                // 给卖家发送提醒
                SystemMessage systemMessage = new SystemMessage();
                systemMessage.setType(SystemMessage.BE_BLACKLIST);
                systemMessage.setReceiverId(sellerId);
                systemMessageSender.sendMsgById(sellerId,systemMessage);
                systemMessageRepository.save(systemMessage);
            } else if(Double.compare(level,0) == 0) {
                // 账号几乎不受损
                // 资金返还给买家
                buyer.setBalance(DecimalUtils.add(buyerBalance,bid));
                userMapper.updateById(buyer);
            } else {
                // 账号中度受损
                // 按比例分配
                BigDecimal ratio = BigDecimal.valueOf(level);
                BigDecimal sellerGet = DecimalUtils.multiply(bid, ratio);
                BigDecimal buyerGet = DecimalUtils.subtract(bid, sellerGet);
                seller.setBalance(DecimalUtils.add(sellerBalance,sellerGet));
                buyer.setBalance(DecimalUtils.add(buyerBalance,buyerGet));
                userMapper.updateById(seller);
                userMapper.updateById(buyer);
            }
            problem.setLevel(level);
            problem.setSolved(1);
            problemMapper.updateById(problem);
            return new ReturnType().success();
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ReturnType().error();
        }
    }
}
