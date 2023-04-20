package com.chen.socketio;

import com.chen.pojo.SystemMessage;
import com.chen.repository.SystemMessageRepository;
import com.corundumstudio.socketio.SocketIOClient;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

// 系统消息发送工具
@Component
public class SystemMessageSender {

    @Resource
    private ClientCache clientCache;

    public void sendMsgById(Long id, SystemMessage message) {
        if (clientCache.isOnline(id)) {
            SocketIOClient client = clientCache.getUserClient(id);
            client.sendEvent(ClientCache.SYSTEM_EVENT,message);
        } else {
            clientCache.addOfflineSystemMessage(id,message);
        }
    }
}
