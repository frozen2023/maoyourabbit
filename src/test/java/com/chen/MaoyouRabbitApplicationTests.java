package com.chen;

import com.chen.mapper.ChatMessageMapper;
import com.chen.mapper.OrderMapper;
import com.chen.repository.UserRepository;
import com.chen.util.SnowFlakeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class MaoyouRabbitApplicationTests {

    @Resource
    private ChatMessageMapper chatMessageMapper;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private UserRepository userRepository;
    @Test
    void contextLoads() {

        /*TestUser testUser = new TestUser();
        testUser.setList(Arrays.asList("nihao","hello"));
        System.out.println(mongoTemplate.insert(testUser));*/

        /*List<TestUser> all = mongoTemplate.findAll(TestUser.class);
        System.out.println(all);*/

        /*TestUser testUser = new TestUser();
        testUser.setId(SnowFlakeUtil.getSnowFlakeId());
        TestUser insert = userRepository.insert(testUser);
        System.out.println(insert);*/
        System.out.println(userRepository.findById(129115899927072768L));
    }

}
