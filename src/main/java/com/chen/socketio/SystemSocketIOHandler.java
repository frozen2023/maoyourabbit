package com.chen.socketio;

import com.chen.pojo.SystemMessage;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
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
        System.out.println("向用户"+getUserIdByClient(client)+"发送了"+sendOfflineMessage(client)+"条离线系统消息");
    }

    public Long getUserIdByClient(SocketIOClient client) {
        String userId = client.getHandshakeData().getSingleUrlParam("clientId");
        return Long.valueOf(userId);
    }

    public int sendOfflineMessage(SocketIOClient client) {
        int cnt = 0;
        Long userId = getUserIdByClient(client);
        Queue<SystemMessage> msgQueue = clientCache.getOfflineMessageQueue(userId);
        if(Objects.isNull(msgQueue) || msgQueue.size() == 0)
            return cnt;
        int length = msgQueue.size();
        for (int i = 0; i < length; i++) {
            SystemMessage message = msgQueue.poll();
            client.sendEvent(message.getEvent(),message);
            cnt++;
        }
        return cnt;
    }
}
