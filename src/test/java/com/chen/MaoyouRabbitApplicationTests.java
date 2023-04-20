package com.chen;

import com.chen.common.EventMsgs;
import com.chen.pojo.Order;
import com.chen.pojo.OrderEvent;
import com.chen.repository.OrderRepository;
import com.chen.util.CloneUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;

import javax.annotation.Resource;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class MaoyouRabbitApplicationTests {

    @Resource
    private OrderRepository orderRepository;
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
        // orderRepository.deleteAll();
        /*System.out.println(orderRepository.findAll());*/
        //System.out.println(OrderEvent.BUYER_GET);
        /*Sort sort = Sort.by(Sort.Direction.ASC, "mgtCreate");
        Pageable pageable = PageRequest.of(0,3,sort);
        Order order = new Order();
        order.setPaid(0);
        *//*ExampleMatcher exampleMatcher = ExampleMatcher.matching().withMatcher("accountId", ExampleMatcher.GenericPropertyMatchers.exact())
                .withIgnorePaths("_class","_id");
        Example<Order> orderExample = Example.of(order,exampleMatcher);
        List<Order> all1 = orderRepository.findAll(Example.of(order));*//*
        Page<Order> orders = orderRepository.findAllBySellerIdAndFinished(102101230L, 0,pageable);
        List<Order> content = orders.getContent();
        System.out.println(content.size());
        for (Order order1 : orders.getContent()) {
            System.out.println(order1);
        }*/

        /*Page<Order> all = orderRepository.findAll(pageable);
        System.out.println(all.getTotalElements());*/
        /*List<Order> content = all.getContent();
        for (Order or : content) {
            System.out.println(or);
        }*/
        /*Order order1 = new Order();
        order1.setOrderId(10101010L);
        order1.setOrderEvents(Arrays.asList(new OrderEvent(EventMsgs.BUYER_PAY)));
        System.out.println(order1);
        Order order2;
        order2 = CloneUtil.deepClone(order1);
        System.out.println(order2);*/
    }

}
