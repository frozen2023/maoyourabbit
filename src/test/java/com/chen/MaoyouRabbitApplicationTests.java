package com.chen;

import com.chen.mapper.ChatMessageMapper;
import com.chen.pojo.ChatMessage;
import com.chen.security.LoginUser;
import com.chen.util.SnowFlakeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.Resource;
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
    }

}
