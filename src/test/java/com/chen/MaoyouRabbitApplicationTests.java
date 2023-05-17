package com.chen;

import com.chen.common.EventMsgs;
import com.chen.pojo.Order;
import com.chen.pojo.OrderEvent;
import com.chen.repository.OrderRepository;
import com.chen.util.CloneUtil;
import com.chen.util.SMSUtils;
import com.chen.util.ValidateCodeUtils;
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
        //获取手机号
        String phone = "18750037207";				//输入手机号,要填绑定测试的手机号码
        //生成验证码
        String code = ValidateCodeUtils.generateValidateCode(6).toString();
        //发送短信
        /**
         * 发送短信
         * @param signName 签名
         * @param templateCode 模板
         * @param phoneNumbers 手机号
         * @param param 参数
         */
        SMSUtils.smsAliyun(phone,code);
    }

}
