package com.chen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.common.ReturnType;
import com.chen.mapper.BidMapper;
import com.chen.pojo.Account;
import com.chen.mapper.AccountMapper;
import com.chen.pojo.Bid;
import com.chen.pojo.SystemMessage;
import com.chen.pojo.User;
import com.chen.service.AccountService;
import com.chen.socketio.ClientCache;
import com.chen.socketio.SystemMessageSender;
import com.chen.util.SnowFlakeUtil;
import com.chen.util.UserGetter;
import com.sun.javafx.collections.MappingChange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.io.ObjectStreamClass;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author Frozen
 * @since 2023-03-22
 */
@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

    @Resource
    private AccountMapper accountMapper;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private SystemMessageSender systemMessageSender;
    @Resource
    private UserGetter userGetter;
    @Resource
    private BidMapper bidMapper;

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
            SystemMessage message = new SystemMessage(ClientCache.EXAMINE_PASS_EVENT,acc,new Date());
            systemMessageSender.sendMsgById(sellerId,message);
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
        SystemMessage message = new SystemMessage(ClientCache.EXAMINE_FAIL_EVENT,data,new Date());
        systemMessageSender.sendMsgById(sellerId,message);
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
        // 给卖家发送提醒
        Map<String,Object> map = new HashMap<>();
        map.put("account",account);
        map.put("buyer",buyer);
        map.put("bid",amount);
        SystemMessage message = new SystemMessage(ClientCache.BUYER_OFFER_EVENT,map,new Date());
        systemMessageSender.sendMsgById(sellerId,message);
        return new ReturnType().success();
    }
}
