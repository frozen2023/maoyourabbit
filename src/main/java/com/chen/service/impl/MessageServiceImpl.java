package com.chen.service.impl;

import com.chen.common.ReturnType;
import com.chen.pojo.ChatMessage;
import com.chen.pojo.SystemMessage;
import com.chen.repository.ChatMessageRepository;
import com.chen.repository.SystemMessageRepository;
import com.chen.service.MessageService;
import com.chen.util.UserGetter;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.message.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageServiceImpl implements MessageService {
    @Resource
    private SystemMessageRepository systemMessageRepository;
    @Resource
    private UserGetter userGetter;
    @Resource
    private ChatMessageRepository chatMessageRepository;

    @Override
    public ReturnType getSystemMessages(Integer page) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        if(page < 1)
            return new ReturnType().error();
        page = page - 1;
        Long receiverId = userGetter.getUserId();
        Pageable pageable = PageRequest.of(page,5,sort);
        Page<SystemMessage> systemMessagePage = systemMessageRepository.findAllByReceiverId(receiverId,pageable);
        int totalPages = systemMessagePage.getTotalPages();
        List<SystemMessage> systemMessages = systemMessagePage.getContent();
        Map<String,Object> data = new HashMap<>();
        data.put("totalPages",totalPages);
        data.put("systemMessages",systemMessages);
        return new ReturnType().success(data);
    }

    @Override
    public ReturnType getChatMessages(Long userId) {
        Long uid = userGetter.getUserId();
        List<ChatMessage> asReceiver = chatMessageRepository.findAllByReceiverIdAndSenderId(uid, userId);
        List<ChatMessage> asSender = chatMessageRepository.findAllByReceiverIdAndSenderId(userId, uid);
        asReceiver.addAll(asSender);
        Map<String,Object> data = new HashMap<>();
        data.put("messages",asReceiver);
        return new ReturnType().success(data);
    }
}
