package com.chen.socketio;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chen.mapper.ChatMessageMapper;
import com.chen.pojo.ChatMessage;
import com.chen.util.ImageUtils;
import com.chen.util.SnowFlakeUtil;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.*;

// 聊天消息处理器

@Slf4j
@Component
public class ChatSocketIOHandler {
    @Resource
    private ClientCache clientCache;
    @Resource
    private ChatMessageMapper chatMessageMapper;
    @Resource
    private ImageUtils imageUtils;

    @OnConnect
    public void onConnect(SocketIOClient client) {
        Long userId = getUserIdByClient(client);
        clientCache.addClient(userId, client);
        log.info("id为:{}的用户连接建立成功!",userId);
        System.out.println("当前在线用户：");
        for (Long id : clientCache.getOnlineUsers()) {
            System.out.println(id);
        }
        // 当用户上线时，给其发送未读消息
        System.out.println("向用户"+userId+"发送了"+sendUnviewedMsg(client)+"条未读聊天消息");

    }

    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        Long userId = getUserIdByClient(client);
        clientCache.deleteUserCacheByUserId(userId);
        log.info("id为:{}的用户连接关闭成功!",userId);
    }

    @OnEvent(ClientCache.CHAT_EVENT)
    public void chatEvent(SocketIOClient client, RcvMessage msg, AckRequest ackRequest) {
        // 文本消息
        ChatMessage data = msg.getMessage();
        data.setMessageId(SnowFlakeUtil.getSnowFlakeId());
        Long senderId = getUserIdByClient(client);
        data.setSenderId(senderId);
        Long receiverId = data.getReceiverId();
        if (data.getType() == 1) {
            System.out.println("接收到"+ senderId +"用户发给"+receiverId+"的文本消息："+data.getInfo());
        }
        // 图片消息
        if (data.getType() == 2) {
            System.out.println("接收到"+ senderId +"用户发给"+receiverId+"的图片消息");
            String fileName = msg.getFileName();
            String base64Image = msg.getImage();
            byte[] bytes = imageUtils.imageToBytes(base64Image);
            String url = imageUtils.uploadImage(bytes, fileName);
            System.out.println("url: "+url);
            data.setImageUrl(url);
        }
        // 在线
        if (clientCache.isOnline(receiverId)) {
            data.setViewed(1);
            sendMsgById(receiverId,data);
        } else {  // 不在线
            data.setViewed(0);
        }
        // 保存消息记录
        chatMessageMapper.insert(data);
    }

    // 根据客户端获取用户id
    public Long getUserIdByClient(SocketIOClient client) {
        String userId = client.getHandshakeData().getSingleUrlParam("clientId");
        return Long.valueOf(userId);
    }

    // 给用户发送未读消息
    public int sendUnviewedMsg(SocketIOClient client) {
        Long receiverId = getUserIdByClient(client);
        QueryWrapper<ChatMessage> wrapper = new QueryWrapper<>();
        wrapper.eq("receiver_id",receiverId).eq("viewed",0);
        List<ChatMessage> msgs = chatMessageMapper.selectList(wrapper);
        for (ChatMessage msg : msgs) {
            client.sendEvent("chatEvent",msg);
            msg.setViewed(1);
            chatMessageMapper.updateById(msg);
        }
        return msgs.size();
    }

    // 根据id发送给客户端
    public void sendMsgById(Long receiverId, ChatMessage msg) {
        SocketIOClient client = clientCache.getUserClient(receiverId);
        client.sendEvent("chatEvent",msg);
    }

}
