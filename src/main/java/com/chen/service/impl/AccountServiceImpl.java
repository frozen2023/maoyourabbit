package com.chen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.common.ReturnType;
import com.chen.mapper.BidMapper;
import com.chen.pojo.*;
import com.chen.mapper.AccountMapper;
import com.chen.repository.SystemMessageRepository;
import com.chen.service.AccountService;
import com.chen.socketio.MessageSender;
import com.chen.util.SnowFlakeUtil;
import com.chen.util.UserGetter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
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
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class AccountServiceImpl implements AccountService {

    @Resource
    private AccountMapper accountMapper;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private MessageSender messageSender;
    @Resource
    private UserGetter userGetter;
    @Resource
    private BidMapper bidMapper;
    @Resource
    private SystemMessageRepository systemMessageRepository;

    @Override
    public ReturnType putAccount(Account account) {
        account.setAccountId(SnowFlakeUtil.getSnowFlakeId());
        account.setAccountPassword(passwordEncoder.encode(account.getAccountNumber()));
        if (accountMapper.insert(account) > 0) {
            log.info("id为{}的用户挂出了一个{}的账号",account.getSellerId(),account.getGameName());
            return new ReturnType().success();
        } else {
            return new ReturnType().error();
        }
    }

    @Override
    public ReturnType getUnauditedAccountList() {
        QueryWrapper<Account> wrapper = new QueryWrapper<>();
        wrapper.eq("varified",0);
        List<Account> accounts = accountMapper.selectList(wrapper);
        Map<String,Object> map = new HashMap<>();
        map.put("accounts",accounts);
        return new ReturnType().success(map);
    }

    @Override
    public ReturnType pass(Account account) {
        account.setVarified(1);
        Long sellerId = account.getSellerId();
        Long accountId = account.getAccountId();
        if (accountMapper.updateById(account) > 0) {
            log.info("订单号为{}的订单经管理员审核成功",accountId);
            Account acc = accountMapper.selectById(accountId);
            // 给卖家发送系统消息
            SystemMessage systemMessage = new SystemMessage();
            systemMessage.setType(SystemMessage.ACCOUNT_VERIFY_RESULT);
            systemMessage.setReceiverId(sellerId);
            Map<String,Object> data = new HashMap<>();
            data.put("account",acc);
            data.put("result",1);
            systemMessage.setData(data);
            messageSender.sendSystemMessageById(sellerId,systemMessage);
            return new ReturnType().success();
        } else {
            return new ReturnType().error();
        }
    }

    @Override
    public ReturnType fail(Long sellerId, Long accountId, String cause) {
        Account account = accountMapper.selectById(accountId);
        Map<String,Object> data = new HashMap<>();
        data.put("cause",cause);
        data.put("account",account);
        SystemMessage systemMessage = new SystemMessage();
        systemMessage.setType(SystemMessage.ACCOUNT_VERIFY_RESULT);
        systemMessage.setReceiverId(sellerId);
        Map<String,Object> map = new HashMap<>();
        data.put("account",account);
        data.put("result",0);
        systemMessage.setData(map);
        messageSender.sendSystemMessageById(sellerId,systemMessage);
        account.setDeleted(1);
        if (accountMapper.deleteById(accountId) > 0) {
            return new ReturnType().success();
        } else {
            return new ReturnType().error();
        }
    }

    @Override
    public ReturnType getListedAccounts(Integer page, Integer varified, Integer bought) {
        Long userId = userGetter.getUserId();
        Page<Account> queryPage = new Page<>(page,5);
        QueryWrapper<Account> wrapper = new QueryWrapper<>();
        wrapper.eq("seller_id",userId);
        if(varified != 2) {
            wrapper.eq("varified",varified);
        }
        if(bought != 2) {
            wrapper.eq("bought",bought);
        }
        // 查询账号
        Page<Account> accountPage = accountMapper.selectPage(queryPage, wrapper);
        long pages = accountPage.getPages();
        List<Account> records = accountPage.getRecords();
        List<Map<String,Object>> result = new ArrayList<>();
        // 查询出价
        for (Account record : records) {
            Long accountId = record.getAccountId();
            QueryWrapper<Bid> bidQueryWrapper = new QueryWrapper<>();
            bidQueryWrapper.eq("account_id",accountId);
            List<Bid> bids = bidMapper.selectList(bidQueryWrapper);
            Map<String,Object> innerMap = new HashMap<>();
            innerMap.put("account",record);
            innerMap.put("bids",bids);
            result.add(innerMap);
        }
        System.out.println("共有"+pages+"页");
        System.out.println(records);
        Map<String,Object> map = new HashMap<>();
        map.put("accounts",result);
        map.put("pages",pages);
        return new ReturnType().success(map);
    }

    @Override
    public ReturnType updateAccount(Account account) {
        try {
            account.setVarified(0);
            accountMapper.updateById(account);
            return new ReturnType().success();
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ReturnType().error();
        }
    }

    @Override
    public ReturnType getPurchasableAccounts(String name, BigDecimal minPrice, BigDecimal maxPrice, String number, Long page) {
        QueryWrapper<Account> wrapper = new QueryWrapper<>();
        Page<Account> queryPage = new Page<>(page,5);
        if (!Objects.isNull(name))
            wrapper.eq("game_name",name);
        if (!Objects.isNull(minPrice))
                wrapper.ge("price",minPrice);
        if(!Objects.isNull(maxPrice))
                wrapper.le("price",maxPrice);
        if(!Objects.isNull(number))
                wrapper.eq("account_number",number);
        wrapper.eq("bought",0);
        wrapper.eq("varified",1);
        Page<Account> accountPage = accountMapper.selectPage(queryPage, wrapper);
        long pages = accountPage.getPages();
        List<Account> records = accountPage.getRecords();
        System.out.println("共有"+pages+"页");
        System.out.println(records);
        Map<String,Object> map = new HashMap<>();
        map.put("accounts",records);
        map.put("pages",pages);
        return new ReturnType().success(map);
    }

    @Override
    public ReturnType offer(Long sellerId, Long accountId, BigDecimal amount) {
        try {
            Account account = accountMapper.selectById(accountId);
            User buyer = userGetter.getUser();
            if (account.getBought() == 1) {
                return new ReturnType().error("账号已被购买");
            }
            // 保存到出价表
            Bid bid = new Bid();
            bid.setBidId(SnowFlakeUtil.getSnowFlakeId());
            bid.setAccountId(accountId);
            bid.setBidderId(buyer.getUserId());
            bid.setSellerId(sellerId);
            bid.setAmount(amount);
            bidMapper.insert(bid);
            return new ReturnType().success();
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ReturnType().error();
        }
    }

    @Override
    public ReturnType updatePrice(Long accountId, BigDecimal prePrice, BigDecimal curPrice) {
        try {
            Account account = accountMapper.selectById(accountId);
            // 降价
            if (prePrice.compareTo(curPrice) == 1) {
                // 给出价者发送降价提醒
                QueryWrapper<Bid> wrapper = new QueryWrapper<>();
                wrapper.eq("account_id",accountId);
                List<Bid> bids = bidMapper.selectList(wrapper);
                System.out.println("bids==>" + bids);
                Map<String,Object> data = new HashMap<>();
                data.put("prePrice",prePrice);
                data.put("curPrice",curPrice);
                data.put("account",account);
                for (Bid bid : bids) {
                    Long bidderId = bid.getBidderId();
                    SystemMessage systemMessage = new SystemMessage();
                    systemMessage.setType(SystemMessage.LOWER_PRICE);
                    systemMessage.setReceiverId(bidderId);
                    systemMessage.setData(data);
                    messageSender.sendSystemMessageById(bidderId,systemMessage);
                    systemMessageRepository.save(systemMessage);
                }
            }
            account.setPrice(curPrice);
            accountMapper.updateById(account);
            return new ReturnType().success();
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ReturnType().error();
        }
    }

    @Override
    public ReturnType deleteAccount(Integer type, Long accountId, List<Long> accountIds) {
        try {
            // 单次删除
            if (type == 1) {
                Account account = accountMapper.selectById(accountId);
                if (account.getBought() == 1) {
                    return new ReturnType().error("账号已被购买");
                }
                accountMapper.deleteById(accountId);
            } else {
                // 批量删除
                for (Long id : accountIds) {
                    Account account = accountMapper.selectById(id);
                    if (account.getBought() == 0) {
                        accountMapper.deleteById(id);
                    }
                }
            }
            return new ReturnType().success();
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ReturnType().error();
        }
    }

    @Override
    public ReturnType getAccountById(Long accountId) {
        Account account = accountMapper.selectById(accountId);
        if (Objects.isNull(account)) {
            return new ReturnType().error("未找到该账号");
        }
        Map<String,Object> data = new HashMap<>();
        data.put("account",account);
        return new ReturnType().success(data);
    }
}
