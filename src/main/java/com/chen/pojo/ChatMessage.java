package com.chen.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@Document("ChatMessage")
public class ChatMessage implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final Integer TYPE_TEXT = 1;
    public static final Integer TYPE_IMAGE = 2;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long senderId; // 发送方id

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long receiverId; // 接收方id

    private Integer type;  //消息类型：1 文本消息 2 图片消息

    private String info; //文本消息的内容

    private String imageUrl; // 保存到服务器后的url

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date mgtCreate; // 发送时间

    public ChatMessage() {
        this.mgtCreate = new Date();
    }
}
