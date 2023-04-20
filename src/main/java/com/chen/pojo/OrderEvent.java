package com.chen.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final Integer EVENT_MESSAGE = 1;
    public static final Integer ACCOUNT_MESSAGE = 2;
    private Integer type;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private Object eventMsg;

    public OrderEvent(String[] eventMsg) {
        this.type = EVENT_MESSAGE;
        this.eventMsg = eventMsg;
        this.createTime = new Date();
    }

    public OrderEvent(Account account) {
        this.type = ACCOUNT_MESSAGE;
        this.eventMsg = account;
        this.createTime = new Date();
    }
}
