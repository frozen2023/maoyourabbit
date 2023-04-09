package com.chen.rabbitMQ;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DelayQueueConfig {

    public static final String NORMAL_EXCHANGE = "normal_exchange";   // 普通交换机
    public static final String DEAD_EXCHANGE = "dead_exchange";     // 死信交换机

    public static final String CANCEL_ORDER_DELAY_QUEUE = "cancel_order_delay_queue"; // 取消订单延时队列
    public static final String CANCEL_ORDER_DELAY_ROUTING_KEY = "cancel_order_delay_routing_key"; // 取消订单延时队列routing_key
    public static final String CANCEL_ORDER_DEAD_QUEUE = "cancel_order_dead_queue";   // 取消订单死信队列
    public static final String CANCEL_ORDER_DEAD_ROUTING_KEY = "cancel_order_dead_routing_key"; // 取消订单死信队列routing_key

    public static final String CHECK_ORDER_DELAY_QUEUE = "check_order_delay_queue"; // 自动确认延时队列
    public static final String CHECK_ORDER_DELAY_ROUTING_KEY = "check_order_delay_routing_key"; // 自动确认延时routing_key
    public static final String CHECK_ORDER_DEAD_QUEUE = "check_order_dead_queue"; // 自动确认死信队列
    public static final String CHECK_ORDER_DEAD_ROUTING_KEY = "check_order_dead_routing_key"; // 自动确认死信routing_key


    // 声明普通交换机
    @Bean
    public DirectExchange normalExchange() {
        return new DirectExchange(NORMAL_EXCHANGE,true,false);
    }

    // 声明死信交换机
    @Bean
    public DirectExchange deadExchange() {
        return new DirectExchange(DEAD_EXCHANGE,true,false);
    }

    /*
    * 声明取消订单延时队列
    * ttl为 10分钟
    * */
    @Bean
    public Queue cancelOrderDelayQueue() {
        return QueueBuilder.durable(CANCEL_ORDER_DELAY_QUEUE)
                .deadLetterExchange(DEAD_EXCHANGE)
                .deadLetterRoutingKey(CANCEL_ORDER_DEAD_ROUTING_KEY)
                .ttl(10000)
                .build();
    }

    /*
    * 声明自动确认延时队列
    * ttl为 10分钟
    * */
    @Bean
    public Queue checkOrderDelayQueue() {
        return QueueBuilder.durable(CHECK_ORDER_DELAY_QUEUE)
                .deadLetterExchange(DEAD_EXCHANGE)
                .deadLetterRoutingKey(CHECK_ORDER_DEAD_ROUTING_KEY)
                .ttl(10000)
                .build();
    }

    /*
    * 声明取消订单死信队列
    * */
    @Bean
    public Queue cancelOrderDeadQueue() {
        return QueueBuilder.durable(CANCEL_ORDER_DEAD_QUEUE).build();
    }

    /*
    * 声明自动确认死信队列
    * */
    @Bean
    public Queue checkOrderDeadQueue() {
        return QueueBuilder.durable(CHECK_ORDER_DEAD_QUEUE).build();
    }

    /*
    * 声明取消订单延时队列和普通交换机的绑定关系
    * */
    @Bean
    public Binding cancelOrderDelayQueueBindingNormalExchange(@Qualifier("cancelOrderDelayQueue") Queue queue,
                                                     @Qualifier("normalExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(CANCEL_ORDER_DELAY_ROUTING_KEY);
    }

    /*
     * 声明自动确认延时队列和普通交换机的绑定关系
     * */
    @Bean
    public Binding checkOrderDelayQueueBindingNormalExchange(@Qualifier("checkOrderDelayQueue") Queue queue,
                                                     @Qualifier("normalExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(CHECK_ORDER_DELAY_ROUTING_KEY);
    }

    /*
    * 声明取消订单死信队列和死信交换机的绑定关系
    * */
    @Bean
    public Binding cancelOrderDeadQueueBindingDeadExchange(@Qualifier("cancelOrderDeadQueue") Queue queue,
                                                   @Qualifier("deadExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(CANCEL_ORDER_DEAD_ROUTING_KEY);
    }

    /*
     * 声明自动确认死信队列和死信交换机的绑定关系
     * */
    @Bean
    public Binding checkOrderDeadQueueBindingDeadExchange(@Qualifier("checkOrderDeadQueue") Queue queue,
                                                           @Qualifier("deadExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(CHECK_ORDER_DEAD_ROUTING_KEY);
    }

}
