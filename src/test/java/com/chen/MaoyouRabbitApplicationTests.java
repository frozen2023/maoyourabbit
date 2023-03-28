package com.chen;

import com.chen.mapper.ChatMessageMapper;
import com.chen.pojo.ChatMessage;
import com.chen.security.LoginUser;
import com.chen.util.SnowFlakeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.Resource;

@SpringBootTest
class MaoyouRabbitApplicationTests {

    @Resource
    private ChatMessageMapper chatMessageMapper;
    @Test
    void contextLoads() {
        ChatMessage message = new ChatMessage();
        message.setMessageId(SnowFlakeUtil.getSnowFlakeId());
        message.setSenderId(1021012302L);
        message.setReceiverId(1021012302L);
        message.setType(1);
        message.setInfo("你好");
        message.setViewed(1);
        chatMessageMapper.insert(message);
    }

}
