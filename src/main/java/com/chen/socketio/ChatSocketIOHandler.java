package com.chen.socketio;

import com.chen.pojo.ChatMessage;
import com.chen.pojo.SystemMessage;
import com.chen.repository.ChatMessageRepository;
import com.chen.util.ImageUtils;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import javax.security.auth.callback.CallbackHandler;
import java.util.Objects;
import java.util.Queue;

// 聊天消息处理器

@Slf4j
@Component
public class ChatSocketIOHandler {
    @Resource
    private ClientCache clientCache;
    @Resource
    private ImageUtils imageUtils;
    @Resource
    private MessageSender messageSender;
    @Resource
    private ChatMessageRepository chatMessageRepository;

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
        sendOfflineChatMessages(client);

    }

    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        Long userId = getUserIdByClient(client);
        clientCache.deleteUserCacheByUserId(userId);
        log.info("id为:{}的用户连接关闭成功!",userId);
    }

    @OnEvent(ClientCache.CHAT_EVENT)
    public void chatEvent(SocketIOClient client, RcvMessage msg, AckRequest ackRequest) {
        ChatMessage chatMessage = msg.getMessage();
        Long senderId = getUserIdByClient(client);
        chatMessage.setSenderId(senderId);
        Long receiverId = chatMessage.getReceiverId();
        // 文本消息
        if (chatMessage.getType() == 1) {
            System.out.println("接收到"+ senderId +"用户发给"+receiverId+"的文本消息："+chatMessage.getInfo());
        }
        // 图片消息
        if (chatMessage.getType() == 2) {
            System.out.println("接收到"+ senderId +"用户发给"+receiverId+"的图片消息");
            String fileName = msg.getFileName();
            String base64Image = msg.getImage();
            byte[] bytes = imageUtils.imageToBytes(base64Image);
            String url = imageUtils.uploadImage(bytes, fileName);
            System.out.println("url: "+url);
            chatMessage.setImageUrl(url);
        }
        // 发送消息
        messageSender.sendChatMessageById(receiverId,chatMessage);
        // 保存消息记录
        chatMessageRepository.save(chatMessage);

    }

    // 根据客户端获取用户id
    public Long getUserIdByClient(SocketIOClient client) {
        String userId = client.getHandshakeData().getSingleUrlParam("clientId");
        return Long.valueOf(userId);
    }

    // 给用户发送未读消息
    public void sendOfflineChatMessages(SocketIOClient client) {
        int cnt = 0;
        Long userId = getUserIdByClient(client);
        Queue<ChatMessage> msgQueue = clientCache.getOfflineChatMessageQueue(userId);
        if(Objects.isNull(msgQueue) || msgQueue.size() == 0)
            return;
        int length = msgQueue.size();
        for (int i = 0; i < length; i++) {
            ChatMessage message = msgQueue.poll();
            client.sendEvent(ClientCache.CHAT_EVENT,message);
            cnt++;
        }
        System.out.println("向用户"+userId+"发送了"+ cnt + "条未读聊天消息");
    }
}
