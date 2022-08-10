package com.socket.socketexample.domain.chatting.request;

import com.socket.socketexample.domain.chatting.enums.MessageType;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageReq {

    private MessageType type;

    private String roomId;

    private String sender;

    private String message;
}
