package com.socket.socketexample.domain.chatting.dto;

import com.socket.socketexample.domain.chatting.enums.MessageType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage {

    private MessageType type;

    private String roomId;

    private String sender;

    private String message;
}
