package com.chen.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    @TableId(type = IdType.INPUT)
    private Long messageId;

    private Long senderId;

    private Long receiverId;

    private String type;

    private String info;

    private String imageUrl;

    @TableField(fill = FieldFill.INSERT)
    private Date mgtCreate;

    @TableLogic
    private Integer deleted;
}
