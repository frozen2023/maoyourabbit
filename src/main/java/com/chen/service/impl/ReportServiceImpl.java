package com.chen.service.impl;

import com.chen.common.Authority;
import com.chen.common.ReturnType;
import com.chen.mapper.DebtMapper;
import com.chen.mapper.UserMapper;
import com.chen.pojo.*;
import com.chen.repository.OrderRepository;
import com.chen.repository.ReportRepository;
import com.chen.repository.SystemMessageRepository;
import com.chen.service.ReportService;
import com.chen.socketio.SystemMessageSender;
import com.chen.util.DecimalUtils;
import com.chen.util.ImageUtils;
import com.chen.util.SnowFlakeUtil;
import com.chen.util.UserGetter;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Frozen
 * @since 2023-03-22
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ReportServiceImpl implements ReportService {
    
    @Resource
    private ImageUtils imageUtils;
    @Resource
    private ReportRepository reportRepository;
    @Resource
    private UserGetter userGetter;
    @Resource
    private UserMapper userMapper;
    @Resource
    private OrderRepository orderRepository;
    @Resource
    private DebtMapper debtMapper;
    @Resource
    private SystemMessageSender systemMessageSender;
    @Resource
    private SystemMessageRepository systemMessageRepository;

    @Override
    public ReturnType addReport(MultipartFile[] images, Report report) {
        try {
            // 将图片上传至七牛云
            List<String> list = imageUtils.uploadImages(images);
            report.setUrls(list);
            Long whistleblowerId = userGetter.getUserId();
            report.setWhistleblowerId(whistleblowerId);
            // 保存到 db
            Report save = reportRepository.save(report);
            System.out.println(save);
            return new ReturnType().success();
        } catch (Exception e) {
            return new ReturnType().error();
        }
    }

    @Override
    public ReturnType getReports(Integer page, Integer handled) {
        if(page < 1)
            return new ReturnType().error();
        page = page - 1;
        int pageSize = 5;
        Pageable pageable = PageRequest.of(page,pageSize);
        Page<Report> reportPage = reportRepository.findAllByHandled(handled, pageable);
        int totalPages = reportPage.getTotalPages();
        List<Report> reports = reportPage.getContent();
        Map<String, Object> data = new HashMap<>();
        data.put("totalPages",totalPages);
        data.put("reports",reports);
        return new ReturnType().success(data);
    }

    @Override
    public ReturnType freeze(Long userId) {
        try {
            // 冻结
            User user = new User();
            user.setUserId(userId);
            user.setFrozen(1);
            userMapper.updateById(user);
            return new ReturnType().success();
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ReturnType().error();
        }
    }

    @Override
    public ReturnType handle(Long reportId, Integer result) {
        Report report = reportRepository.findById(reportId).get();
        Report preReport = new Report();
        try {
            BeanUtils.copyProperties(preReport,report);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            if(report.getHandled() == 1) {
                return new ReturnType().error();
            }
            Integer type = report.getType();
            Long sellerId = report.getDefendantId();
            Long buyerId = report.getWhistleblowerId();
            // 审核属实
            if(result == 1) {
                User seller = userMapper.selectById(sellerId);
                // 找回账号
                if(type == 1) {
                    Long orderId = report.getOrderId();
                    Order order = orderRepository.findById(orderId).get();
                    BigDecimal bid = order.getBid();
                    Long accountId = order.getAccountId();
                    BigDecimal sellerBalance = seller.getBalance();
                    User buyer = userMapper.selectById(buyerId);
                    BigDecimal buyerBalance = buyer.getBalance();
                    // 买家余额返还
                    buyerBalance = DecimalUtils.add(buyerBalance,bid);
                    buyer.setBalance(buyerBalance);
                    userMapper.updateById(buyer);
                    // 卖家余额充足
                    if(sellerBalance.compareTo(bid) > -1) {
                        // 余额减少
                        sellerBalance = DecimalUtils.subtract(sellerBalance,bid);
                        seller.setBalance(sellerBalance);
                        // 解冻钱包
                        seller.setFrozen(0);
                        // 加入黑名单
                        toBlackList(seller);
                        userMapper.updateById(seller);
                    } else { // 卖家余额不足
                        seller.setBalance(BigDecimal.valueOf(0.00));
                        toBlackList(seller);
                        userMapper.updateById(seller);
                        // 加入讨债名单
                        Debt debt = new Debt();
                        debt.setDebtId(SnowFlakeUtil.getSnowFlakeId());
                        debt.setDebtorId(sellerId);
                        BigDecimal subtract = DecimalUtils.subtract(bid, sellerBalance);
                        debt.setAmount(subtract);
                        debt.setAccountId(accountId);
                        debtMapper.insert(debt);
                    }
                } else { // 其他举报
                    //加入黑名单
                    toBlackList(seller);
                    userMapper.updateById(seller);
                }
                report.setResult(result);
                report.setHandlingTime(new Date());
                report.setHandled(1);
                reportRepository.save(report);
            } else { // 审核不通过
                report.setResult(result);
                report.setHandled(1);
                report.setHandlingTime(new Date());
                reportRepository.save(report);
            }
            // 发送提醒
            SystemMessage systemMessage = new SystemMessage();
            systemMessage.setType(SystemMessage.REPORT_RESULT);
            systemMessage.setReceiverId(buyerId);
            systemMessage.setData(report);
            systemMessageSender.sendMsgById(buyerId,systemMessage);
            systemMessageRepository.save(systemMessage);
            return new ReturnType().success();
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            reportRepository.save(preReport);
            return new ReturnType().error();
        }
    }

    @Override
    public void toBlackList(User user) {
        // 权限更改
        user.setAuthority(Authority.BLACKLIST);
        // 头像更改
        user.setHeadUrl(Authority.DEFAULT_HEAD);
        // 昵称更改
        Long userId = user.getUserId();
        user.setNickname("用户" + userId);
        SystemMessage blkMsg = new SystemMessage();
        blkMsg.setType(SystemMessage.BE_BLACKLIST);
        blkMsg.setReceiverId(userId);
        systemMessageSender.sendMsgById(userId,blkMsg);
        systemMessageRepository.save(blkMsg);
    }
}
