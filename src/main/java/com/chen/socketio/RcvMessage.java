package com.chen.socketio;

import com.chen.pojo.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RcvMessage {
    private ChatMessage message;
    private String image;
    private String fileName;
}
