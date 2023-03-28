package com.chen.socketio;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

@Slf4j
@Service
public class SocketIOService {

    @Resource
    private SocketIOServer socketIOServer;

    @PostConstruct
    public void autoStart() {
        socketIOServer.start();
        log.info("SocketIOServer已启动.....");
    }

    @PreDestroy
    public void autoStop() {
        if (socketIOServer != null) {
            socketIOServer.stop();
            log.info("SocketIOServer已关闭.....");
            socketIOServer = null;
        }
    }
}
