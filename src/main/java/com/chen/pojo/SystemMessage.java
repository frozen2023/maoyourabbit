package com.chen.pojo;

import com.chen.socketio.ClientCache;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /*private Long receiverId; // 接收方id*/

    private String event; // 事件类型

    private Object data; // 数据

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime; //创建时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date expirationTime; // 过期时间

    public static SystemMessage create(String event, Object data) {
        SystemMessage systemMessage = new SystemMessage();
        systemMessage.setEvent(event);
        systemMessage.setData(data);
        Date curDate = new Date();
        Date expireDate = null;
        if(ClientCache.PAY_EVENT.equals(event)) {
            expireDate = new Date(curDate.getTime() + ClientCache.PAY_EXPIRATION);
        }
        if(ClientCache.CHECK_EVENT.equals(event)) {
            expireDate = new Date(curDate.getTime() + ClientCache.CHECK_EXPIRATION);
        }
        if(ClientCache.CANCEL_TRANSACTION_EVENT.equals(event)) {
            expireDate = new Date(curDate.getTime() + ClientCache.CANCEL_TRANSACTION_EXPIRATION);
        }
        systemMessage.setCreateTime(curDate);
        systemMessage.setExpirationTime(expireDate);
        return systemMessage;

    }

    public boolean isExpired() {
        if(Objects.isNull(expirationTime)) {
            return false;
        }
        return expirationTime.before(new Date());
    }

}
