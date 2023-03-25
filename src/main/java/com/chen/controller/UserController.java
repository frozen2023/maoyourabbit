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

    @PostMapping("/user/register")
    public ReturnType register(@Validated @RequestBody User user) {
        return userService.register(user);
    }

    @PostMapping("/user/login")
    public ReturnType login(@Validated @RequestBody User user) {
        return userService.login(user);
    }


    @PostMapping("/user/logout")
    public ReturnType logout() {
        return userService.logout();
    }

    @IsUser
    @PutMapping("/user/head")
    public ReturnType head(@RequestPart(value = "image") MultipartFile image) {
        return userService.head(image);
    }

    @Common
    @GetMapping("/user")
    public ReturnType user() {
        return userService.getUserDetails();
    }

    @IsUser
    @PutMapping("/user/pwd")
    public ReturnType pwd(@RequestBody Map map) {
        String newPwd = (String) map.get("newPwd");
        return userService.updatePwd(newPwd);
    }

    @IsUser
    @PutMapping("/user/email")
    public ReturnType email(@RequestBody Map map) {
        String email = (String) map.get("email");
        return userService.email(email);
    }

    @IsUser
    @PutMapping("user/name")
    public ReturnType name(@RequestBody Map map) {
        String username = (String) map.get("username");
        String nickname = (String) map.get("nickname");
        return userService.updateName(username,nickname);
    }

    @IsUser
    @PutMapping("/user/auth")
    public ReturnType auth(@RequestBody Map map) {
        String realName = (String) map.get("realName");
        String identityCard = (String) map.get("identityCard");
        return userService.auth(realName,identityCard);
    }

    @IsUser
    @PutMapping("/user/phone")
    public ReturnType phone(@RequestBody Map map) {
        String phone = (String) map.get("phoneNumber");
        return userService.bindingPhone(phone);
    }
}
