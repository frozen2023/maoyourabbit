package com.chen.service;

import com.chen.common.ReturnType;
import com.chen.pojo.Account;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;

/**
 * @author Frozen
 * @since 2023-03-22
 */
public interface AccountService {
    ReturnType putAccount(Account account);
    ReturnType getUnauditedAccountList();
    ReturnType pass(Account account);
    ReturnType fail(Long sellerId, Long accountId, String cause);
    ReturnType getListedAccounts(Integer page, Integer varified, Integer bought);
    ReturnType updateAccount(Account account);
    ReturnType getPurchasableAccounts(String name, BigDecimal minPrice, BigDecimal maxPrice, String number, Long page);
    ReturnType offer(Long sellerId, Long accountId, BigDecimal bid);
    ReturnType updatePrice(Account account);
    ReturnType getAccountById(Long accountId);
}
