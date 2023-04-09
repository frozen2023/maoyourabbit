package com.chen;

import com.chen.mapper.ChatMessageMapper;
import com.chen.pojo.ChatMessage;
import com.chen.pojo.SystemMessage;
import com.chen.security.LoginUser;
import com.chen.socketio.ClientCache;
import com.chen.util.ObjectUtils;
import com.chen.util.SnowFlakeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class MaoyouRabbitApplicationTests {

    @Resource
    private ChatMessageMapper chatMessageMapper;
    @Test
    void contextLoads() {
        /*Map<String,Object> map=new HashMap<>();
        map.put("xxx",8);

        Long a= Long.valueOf( map.get("xxx").toString());
        System.out.println(a);*/
        /*Map<String, Object> map = new HashMap<>();
        map.put("msg","test");
        SystemMessage systemMessage = new SystemMessage(ClientCache.PAY_EVENT,map,new Date());
        Map<String, Object> data = ObjectUtils.toMap(systemMessage.getData());
        System.out.println(data.toString());*/
    }

}
