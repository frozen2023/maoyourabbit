package com.chen.controller;

import com.chen.common.ReturnType;
import com.chen.pojo.User;
import com.chen.service.UserService;
import com.chen.util.ImageUtils;
import com.sun.media.sound.SoftTuning;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Arrays;

/**

 * @author Frozen
 * @since 2023-03-22
 */
@RestController
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    private ImageUtils imageUtils;

    @PostMapping("/user")
    public ReturnType register(@Validated @RequestBody User user){
        return userService.register(user);
    }

    @PostMapping("/user/login")
    public ReturnType login(@Validated @RequestBody User user){
        return userService.login(user);
    }

    @PostMapping("/user/logout")
    public ReturnType logout(){
        return userService.logout();
    }
    /*@PostMapping("/admin")
    public String test(){
        return "ok";
    }
    @PostMapping("/image")
    public ReturnType image(@RequestPart(value = "file") MultipartFile image){
        MultipartFile[] multipartFiles = {image};
        System.out.println(image.getOriginalFilename());
        return new ReturnType().code(200).message("success").data(imageUtils.uploadImage(image));

    }*/
}
