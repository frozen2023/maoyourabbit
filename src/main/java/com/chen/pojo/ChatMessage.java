package com.chen.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {

    @TableId(type = IdType.INPUT)
    private Long messageId; // 消息id

    private Long senderId; // 发送方id

    private Long receiverId; // 接收方id

    private Integer type;  //消息类型：1 文本消息 2 图片消息

    private String info; //文本消息的内容

    private String imageUrl; // 保存到服务器后的url

    private Integer viewed; // 1 已读 2未读

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date mgtCreate; // 发送时间

    @TableLogic
    private Integer deleted;
}
