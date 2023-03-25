package com.chen.socketio;

import com.corundumstudio.socketio.SocketIOClient;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ClientCache {

    public static final String CHAT_EVENT = "chatEvent";

    // userId + sessionId 定位到唯一客户端
    private static Map<Long, HashMap<UUID, SocketIOClient>> clients = new ConcurrentHashMap<>();

    //添加客户端
    public void addClient(Long userId,UUID sessionId,SocketIOClient socketIOClient) {
        HashMap<UUID, SocketIOClient> map = clients.get(userId);
        if(Objects.isNull(map)) {
            map = new HashMap<>();
        }
        map.put(sessionId,socketIOClient);
        clients.put(userId,map);
    }

    //获取用户的页面通道信息
    public HashMap<UUID,SocketIOClient> getUserClient(Long userId) {
        return clients.get(userId);
    }

    //获取客户端
    public SocketIOClient getUserClient(Long userId,UUID sessionId) {
        return getUserClient(userId).get(sessionId);
    }

    //根据用户Id及页面sessionID删除页面通道连接
    public void deleteSessionClientByUserId(Long userId,UUID sessionId){
        clients.get(userId).remove(sessionId);
    }

    //根据用户Id删除用户通道连接缓存 暂无使用
    public void deleteUserCacheByUserId(Long userId){
        clients.remove(userId);
    }

    //获取在线的客户端数目
    public int clientCount() {
        int count = 0;
        for (Map.Entry<Long, HashMap<UUID, SocketIOClient>> entry : clients.entrySet()) {
            count += entry.getValue().size();
        }
        return count;
    }

}
