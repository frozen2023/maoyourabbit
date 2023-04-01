package com.chen.controller;

import com.chen.common.ReturnType;
import com.chen.pojo.Account;
import com.chen.security.annotations.IsAdmin;
import com.chen.security.annotations.IsUser;
import com.chen.service.AccountService;
import io.lettuce.core.dynamic.output.CodecAwareOutputFactoryResolver;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author Frozen
 * @since 2023-03-22
 */
@RestController
public class AccountController {
    @Resource
    private AccountService accountService;

    // 挂出账号
    @IsUser
    @PostMapping("/account")
    public ReturnType putAccount(@RequestBody Account account) {
        return accountService.putAccount(account);
    }

    // 获取未审核的账号
    @IsAdmin
    @GetMapping("/account/unaudited")
    public ReturnType getAccountList() {
        return accountService.getUnauditedAccountList();
    }

    // 审核通过
    @IsAdmin
    @PutMapping("/account/pass")
    public ReturnType pass(@RequestBody Account account) {
        return accountService.pass(account);
    }

    // 审核不通过
    @IsAdmin
    @PutMapping("/account/fail")
    public ReturnType fail(@RequestBody Map map) {
        Long sellerId = Long.valueOf(map.get("sellerId").toString());
        Long accountId = Long.valueOf(map.get("accountId").toString());
        String cause = map.get("cause").toString();
        return accountService.fail(sellerId,accountId, cause);
    }
}
