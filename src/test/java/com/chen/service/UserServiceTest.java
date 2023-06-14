package com.chen.service;

import com.chen.common.ReturnType;
import com.chen.pojo.User;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.swing.*;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
class UserServiceTest {
    @Resource
    private UserService userService;

    @Test
    void register() {
        User user = new User();
        user.setUsername("frozen-test");
        user.setPassword("123456");
        ReturnType ret = userService.register(user);
        System.out.println(ret);
    }

    @Test
    void sendLoginCode() {
    }

    @Test
    void login() {
        User user = new User();
        user.setUsername("frozen-test");
        user.setPassword("123456");
        ReturnType ret = userService.login(user);
        System.out.println(ret);
    }

    @Test
    void logout() {
        
    }

    @Test
    void head() {
    }

    @Test
    void email() {
    }

    @Test
    void getUserDetails() {
    }

    @Test
    void updatePwd() {
    }

    @Test
    void updateName() {
    }

    @Test
    void auth() {
    }

    @Test
    void bindingPhone() {
    }

    @Test
    void getUserById() {
    }
}