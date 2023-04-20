package com.chen.socketio;

import com.chen.pojo.SystemMessage;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import org.apache.tomcat.util.security.Escape;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.Objects;
import java.util.Queue;

// 系统消息处理器

@Component
public class SystemSocketIOHandler {

    @Resource
    private ClientCache clientCache;

    @OnConnect
    public void connect(SocketIOClient client) {
        sendOfflineMessage(client);
    }

    public Long getUserIdByClient(SocketIOClient client) {
        String userId = client.getHandshakeData().getSingleUrlParam("clientId");
        return Long.valueOf(userId);
    }

    public void sendOfflineMessage(SocketIOClient client) {
        int cnt = 0;
        Long userId = getUserIdByClient(client);
        Queue<SystemMessage> msgQueue = clientCache.getOfflineMessageQueue(userId);
        if(Objects.isNull(msgQueue) || msgQueue.size() == 0)
            return;
        int length = msgQueue.size();
        for (int i = 0; i < length; i++) {
            SystemMessage message = msgQueue.poll();
            assert message != null;
            client.sendEvent(ClientCache.SYSTEM_EVENT,message);
            System.out.println(message);
            cnt++;
        }
        System.out.println("向用户" + userId + "发送了" + cnt + "条离线系统消息");
    }
}
