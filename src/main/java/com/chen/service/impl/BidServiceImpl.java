package com.chen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.common.ReturnType;
import com.chen.mapper.AccountMapper;
import com.chen.mapper.BidMapper;
import com.chen.pojo.Account;
import com.chen.pojo.Bid;
import com.chen.service.BidService;
import com.chen.util.UserGetter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BidServiceImpl implements BidService {
    @Resource
    private BidMapper bidMapper;
    @Resource
    private UserGetter userGetter;
    @Resource
    private AccountMapper accountMapper;

    @Override
    public ReturnType getBids(Integer page) {
        if (page <= 0) {
            return new ReturnType().error();
        }
        Page<Bid> bidPage = new Page<>(page,10);
        Long bidderId = userGetter.getUserId();
        QueryWrapper<Bid> wrapper = new QueryWrapper<>();
        wrapper.eq("bidder_id",bidderId);
        Page<Bid> selectPage = bidMapper.selectPage(bidPage, wrapper);
        long pages = selectPage.getPages();
        List<Bid> records = selectPage.getRecords();
        List<Map<String,Object>> bidAndAccounts = new ArrayList<>();
        for (Bid record : records) {
            Long accountId = record.getAccountId();
            Account account = accountMapper.selectById(accountId);
            Map<String,Object> innerMap = new HashMap<>();
            innerMap.put("bid",record);
            innerMap.put("account",account);
            bidAndAccounts.add(innerMap);
        }
        Map<String,Object> data = new HashMap<>();
        data.put("bids",bidAndAccounts);
        data.put("pages",pages);
        return new ReturnType().success(data);
    }

    @Override
    public ReturnType deleteBid(Integer type, Long bidId, List<Long> bidIds) {
        try {
            // 单次删除
            if (type == 1) {
                bidMapper.deleteById(bidId);
            } else {
                // 批量删除
                for (Long id : bidIds) {
                    accountMapper.deleteById(id);
                }
            }
            return new ReturnType().success();
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ReturnType().error();
        }
    }
}
