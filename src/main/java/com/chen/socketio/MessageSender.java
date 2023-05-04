package com.chen.socketio;

import com.chen.pojo.ChatMessage;
import com.chen.pojo.SystemMessage;
import com.corundumstudio.socketio.SocketIOClient;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.List;

// 系统消息发送工具
@Component
public class MessageSender {

    @Resource
    private ClientCache clientCache;

    public void sendSystemMessageById(Long id, SystemMessage message) {
        if (clientCache.isOnline(id)) {
            List<SocketIOClient> userClients = clientCache.getUserClient(id);
            for (SocketIOClient userClient : userClients) {
                userClient.sendEvent(ClientCache.SYSTEM_EVENT,message);
            }
        } else {
            clientCache.addOfflineSystemMessage(id,message);
        }
    }

    public void sendChatMessageById(Long id, ChatMessage message) {
        if (clientCache.isOnline(id)) {
            List<SocketIOClient> userClients = clientCache.getUserClient(id);
            for (SocketIOClient userClient : userClients) {
                userClient.sendEvent(ClientCache.CHAT_EVENT,message);
            }
        } else {
            clientCache.addOfflineChatMessage(id,message);
        }
    }
}
