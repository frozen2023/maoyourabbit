package com.chen.socketio;

import com.chen.pojo.Message;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.UUID;

@Slf4j
@Component
public class ChatSocketIOHandler {
    @Resource
    private ClientCache clientCache;

    @OnConnect
    public void onConnect(SocketIOClient client){
        Long userId = getUserIdByClient(client);
        UUID sessionId = client.getSessionId();
        clientCache.addClient(userId,sessionId,client);
        log.info("id为:{}的用户连接建立成功!{}",userId,sessionId);
    }

    @OnDisconnect
    public void onDisconnect(SocketIOClient client){
        Long userId = getUserIdByClient(client);
        UUID sessionId = client.getSessionId();
        clientCache.deleteSessionClientByUserId(userId,sessionId);
        log.info("id为:{}的用户连接关闭成功!{}",userId,sessionId);
    }

    @OnEvent(ClientCache.CHAT_EVENT)
    public void chatEvent(SocketIOClient client, AckRequest ackRequest, Message message) {

    }

    public Long getUserIdByClient(SocketIOClient client) {
        String userId = client.getHandshakeData().getSingleUrlParam("userId");
        return Long.valueOf(userId);
    }

}
