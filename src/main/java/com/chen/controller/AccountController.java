package com.chen.controller;

import com.chen.common.ReturnType;
import com.chen.pojo.Account;
import com.chen.security.annotations.IsAdmin;
import com.chen.security.annotations.IsUser;
import com.chen.service.AccountService;
import com.chen.util.DecimalUtils;
import com.chen.util.ObjectUtils;;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        Long sellerId = ObjectUtils.toLong(map.get("sellerId"));
        Long accountId = ObjectUtils.toLong(map.get("accountId"));
        String cause = ObjectUtils.toString(map.get("cause"));
        return accountService.fail(sellerId,accountId,cause);
    }

    // 卖家查看挂出的账号
    @IsUser
    @GetMapping("/account/seller/{page}/{varified}/{bought}")
    public ReturnType getListedAccounts(@PathVariable("page") Integer page,
                                @PathVariable("varified") Integer varified,
                                @PathVariable("bought")  Integer bought) {
        return accountService.getListedAccounts(page,varified,bought);
    }

    // 卖家修改账号信息
    @IsUser
    @PutMapping("/account")
    public ReturnType updateAccount(@RequestBody Account account) {
        return accountService.updateAccount(account);
    }

    // 买家获取账号列表
    @IsUser
    @PostMapping("/account/buyer")
    public ReturnType getPurchasableAccounts(@RequestBody Map map) {
        String name = ObjectUtils.toString(map.get("name"));
        BigDecimal minPrice = DecimalUtils.toBigDecimal(map.get("minPrice"),2);
        BigDecimal maxPrice = DecimalUtils.toBigDecimal(map.get("maxPrice"),2);
        String number = ObjectUtils.toString(map.get("number"));
        Long page = ObjectUtils.toLong(map.get("page"));
        // System.out.println(name+minPrice+maxPrice+number+page);
        return accountService.getPurchasableAccounts(name,minPrice,maxPrice,number,page);
    }

    // 买家出价
    @IsUser
    @PostMapping("/buyer/offer")
    public ReturnType offer(@RequestBody Map map) {
        Long sellerId = ObjectUtils.toLong(map.get("sellerId"));
        Long accountId = ObjectUtils.toLong(map.get("accountId"));
        BigDecimal bid = DecimalUtils.toBigDecimal(map.get("bid"),2);
        return accountService.offer(sellerId,accountId,bid);
    }

    // 卖家改价
    @IsUser
    @PutMapping("/account/price")
    public ReturnType updatePrice(@RequestBody Map map) {
        Long accountId = ObjectUtils.toLong(map.get("accountId"));
        BigDecimal prePrice = DecimalUtils.toBigDecimal(map.get("prePrice"));
        BigDecimal curPrice = DecimalUtils.toBigDecimal(map.get("curPrice"));
        return accountService.updatePrice(accountId, prePrice, curPrice);
    }

    // 卖家删除账号
    @IsUser
    @DeleteMapping("/account")
    public ReturnType deleteAccount(@RequestBody Map map) {
        Integer type = ObjectUtils.toInteger(map.get("type"));
        Long accountId = ObjectUtils.toLong(map.get("accountId"));
        List<Long> accountIds = ObjectUtils.toLongList(map.get("accountIds"));
        return accountService.deleteAccount(type, accountId, accountIds);
    }

    // 根据id查找账号
    @IsAdmin
    @GetMapping("/account/{accountId}")
    public ReturnType getAccountById(@PathVariable("accountId") Long accountId) {
        return accountService.getAccountById(accountId);
    }
}
