package com.socket.socketexample.domain.chatting.response;

import com.socket.socketexample.domain.chatting.enums.MessageType;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageRes {

    @NonNull
    private MessageType type;
    @NonNull
    private String roomId;
    @NonNull
    private String sender;

    private String message;
}
