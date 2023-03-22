package com.chen.controller;

import com.chen.common.ReturnType;
import com.chen.pojo.User;
import com.chen.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**

 * @author Frozen
 * @since 2023-03-22
 */
@RestController
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/user")
    public ReturnType register(@Validated @RequestBody User user){
        return userService.register(user);
    }

    @PostMapping("/user/login")
    public ReturnType login(@Validated @RequestBody User user){
        return userService.login(user);
    }

}
