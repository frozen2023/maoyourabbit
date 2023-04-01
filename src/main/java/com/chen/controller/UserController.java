package com.chen.controller;

import com.chen.common.ReturnType;
import com.chen.pojo.User;
import com.chen.security.annotations.Common;
import com.chen.security.annotations.IsUser;
import com.chen.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import java.util.Map;


/**

 * @author Frozen
 * @since 2023-03-22
 */
@RestController
public class UserController {

    @Resource
    private UserService userService;

    // 注册
    @PostMapping("/user/register")
    public ReturnType register(@Validated @RequestBody User user) {
        return userService.register(user);
    }

    // 登录
    @PostMapping("/user/login")
    public ReturnType login(@Validated @RequestBody User user) {
        return userService.login(user);
    }

    // 登出
    @PostMapping("/user/logout")
    public ReturnType logout(@RequestBody Map map) {
        Long userId = (Long) map.get("userId");
        return userService.logout(userId);
    }

    // 修改头像
    @IsUser
    @PutMapping("/user/head")
    public ReturnType head(@RequestPart(value = "image") MultipartFile image) {
        return userService.head(image);
    }

    // 获取个人信息
    @Common
    @GetMapping("/user")
    public ReturnType user() {
        return userService.getUserDetails();
    }

    // 修改密码
    @IsUser
    @PutMapping("/user/pwd")
    public ReturnType pwd(@RequestBody Map map) {
        String newPwd = (String) map.get("newPwd");
        return userService.updatePwd(newPwd);
    }

    // 绑定邮箱
    @IsUser
    @PutMapping("/user/email")
    public ReturnType email(@RequestBody Map map) {
        String email = (String) map.get("email");
        return userService.email(email);
    }

    // 修改用户名，昵称
    @IsUser
    @PutMapping("user/name")
    public ReturnType name(@RequestBody Map map) {
        String username = (String) map.get("username");
        String nickname = (String) map.get("nickname");
        return userService.updateName(username,nickname);
    }

    // 实名认证
    @IsUser
    @PutMapping("/user/auth")
    public ReturnType auth(@RequestBody Map map) {
        String realName = (String) map.get("realName");
        String identityCard = (String) map.get("identityCard");
        return userService.auth(realName,identityCard);
    }

    // 绑定手机号
    @IsUser
    @PutMapping("/user/phone")
    public ReturnType phone(@RequestBody Map map) {
        String phone = (String) map.get("phoneNumber");
        return userService.bindingPhone(phone);
    }
}
