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
public class SystemMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /*private Long receiverId; // 接收方id*/

    private String event; // 事件类型

    private Object data; // 数据

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime; //创建时间

}
