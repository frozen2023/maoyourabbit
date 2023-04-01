package com.chen.service;

import com.chen.common.ReturnType;
import com.chen.pojo.Account;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author Frozen
 * @since 2023-03-22
 */
public interface AccountService {
    ReturnType putAccount(Account account);
    ReturnType getUnauditedAccountList();
    ReturnType pass(Account account);
    ReturnType fail(Long sellerId, Long accountId, String cause);
}
