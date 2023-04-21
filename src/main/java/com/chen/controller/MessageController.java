package com.chen.controller;

import com.chen.common.ReturnType;
import com.chen.security.annotations.Common;
import com.chen.service.MessageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class MessageController {
    @Resource
    private MessageService messageService;

    // 获取系统消息
    @Common
    @GetMapping("/systemMessage/{page}")
    public ReturnType getSystemMessages(@PathVariable("page") Integer page) {
        return messageService.getSystemMessages(page);
    }

    // 获取聊天记录
    @Common
    @GetMapping("/chatMessage/{userId}")
    public ReturnType getChatMessages(@PathVariable("userId") Long userId) {
        return messageService.getChatMessages(userId);
    }
}
