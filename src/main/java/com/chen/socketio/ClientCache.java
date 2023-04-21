package com.chen.socketio;

import com.chen.pojo.ChatMessage;
import com.chen.pojo.SystemMessage;
import com.corundumstudio.socketio.SocketIOClient;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

// SocketIO 资源仓库
@Component
public class ClientCache {
    public static final String CHAT_EVENT = "chatEvent"; // 聊天事件
    public static final String SYSTEM_EVENT = "systemEvent"; // 系统通知

    // 离线系统消息暂存
    private static Map<Long, Queue<SystemMessage>> offlineSystemMessages = new ConcurrentHashMap<>();

    // 离线聊天消息暂存
    private static Map<Long, Queue<ChatMessage>> offlineChatMessages = new ConcurrentHashMap<>();

    // userId 定位到唯一客户端,用户只能单端登录
    private static Map<Long,SocketIOClient> clients = new ConcurrentHashMap<>();

    //添加客户端
    public void addClient(Long userId, SocketIOClient socketIOClient) {
        if(!isOnline(userId)) {
            clients.put(userId,socketIOClient);
        }
    }

    //获取用户的页面通道信息
    public SocketIOClient getUserClient(Long userId) {
        return clients.get(userId);
    }

    //根据用户Id删除用户通道连接缓存 暂无使用
    public void deleteUserCacheByUserId(Long userId) {
        clients.remove(userId);
    }

    //获取在线的客户端数目
    public int clientCount() {
        return clients.size();
    }

    // 获取在线用户
    public Set<Long> getOnlineUsers() {
        return clients.keySet();
    }

    // 判断是否在线
    public boolean isOnline(Long receiverId) {
        SocketIOClient userClient = getUserClient(receiverId);
        if (!Objects.isNull(userClient)) {
            return true;
        }
        return false;
    }

    // 将离线系统消息加入队列中
    public void addOfflineSystemMessage(Long receiverId, SystemMessage message) {
        Queue<SystemMessage> queue = offlineSystemMessages.get(receiverId);
        if(Objects.isNull(queue)) {
            queue = new ConcurrentLinkedQueue<>();
        }
        queue.add(message);
        offlineSystemMessages.put(receiverId,queue);
    }

    // 将离线聊天消息暂存
    public void addOfflineChatMessage(Long receiverId, ChatMessage message) {
        Queue<ChatMessage> queue = offlineChatMessages.get(receiverId);
        if(Objects.isNull(queue)) {
            queue = new ConcurrentLinkedQueue<>();
        }
        queue.add(message);
        offlineChatMessages.put(receiverId,queue);
    }

    // 获取离线系统消息队列
    public Queue<SystemMessage> getOfflineSystemMessageQueue(Long id) {
        return offlineSystemMessages.get(id);
    }

    // 获取离线聊天消息队列
    public Queue<ChatMessage> getOfflineChatMessageQueue(Long id) {
        return offlineChatMessages.get(id);
    }

}
