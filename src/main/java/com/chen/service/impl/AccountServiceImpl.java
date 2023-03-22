package com.chen.service.impl;

import com.chen.pojo.Account;
import com.chen.mapper.AccountMapper;
import com.chen.service.AccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Frozen
 * @since 2023-03-22
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

}
