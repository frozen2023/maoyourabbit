package com.chen.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Data
@AllArgsConstructor
@Document("SystemMessage")
public class SystemMessage implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final Integer AUTO_CANCEL = 1;
    public static final Integer AUTO_CHECK = 2;
    public static final Integer AUTO_CANCEL_TRANSACTION = 3;
    public static final Integer ACCOUNT_VERIFY_RESULT = 4;
    public static final Integer BE_BLACKLIST = 5;
    public static final Integer REPORT_RESULT = 6;
    public static final Integer LOWER_PRICE = 7;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long receiverId; // 接收方id

    private Integer type; // 类型

    private Object data; // 数据

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime; //创建时间

    public SystemMessage() {
        this.createTime = new Date();
    }


}
