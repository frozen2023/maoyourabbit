package com.chen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chen.common.ReturnType;
import com.chen.pojo.Account;
import com.chen.mapper.AccountMapper;
import com.chen.pojo.SystemMessage;
import com.chen.service.AccountService;
import com.chen.socketio.ClientCache;
import com.chen.socketio.SystemMessageSender;
import com.chen.util.SnowFlakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Override
    public ReturnType putAccount(Account account) {
        account.setAccountId(SnowFlakeUtil.getSnowFlakeId());
        account.setAccountPassword(passwordEncoder.encode(account.getAccountNumber()));
        if (accountMapper.insert(account) > 0) {
            log.info("id为{}的用户挂出了一个{}的账号",account.getSellerId(),account.getGameName());
            return new ReturnType().code(200).message("操作成功");
        } else {
            return new ReturnType().code(404).message("操作失败");
        }
    }

    @Override
    public ReturnType getUnauditedAccountList() {
        QueryWrapper<Account> wrapper = new QueryWrapper<>();
        wrapper.eq("varified",0);
        List<Account> accounts = accountMapper.selectList(wrapper);
        Map<String,Object> map = new HashMap<>();
        map.put("accounts",accounts);
        return new ReturnType().code(200).message("操作成功").data(map);
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
            return new ReturnType().code(200).message("操作成功");
        } else {
            return new ReturnType().code(404).message("操作失败");
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
            return new ReturnType().code(200).message("操作成功");
        } else {
            return new ReturnType().code(404).message("操作失败");
        }
    }
}
