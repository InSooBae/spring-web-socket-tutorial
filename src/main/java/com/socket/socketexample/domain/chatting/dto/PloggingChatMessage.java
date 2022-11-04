package com.socket.socketexample.domain.chatting.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.socket.socketexample.domain.chatting.enums.MessageType;
import com.socket.socketexample.domain.chatting.enums.PingType;
import lombok.*;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class PloggingChatMessage extends ChatMessage {

    @Builder
    public PloggingChatMessage(MessageType type, String roomId, String sender, String message, long userCount, PingType pingType, String lat, String lng) {
        super(type, roomId, sender, message, userCount);
        this.pingType = pingType;
        this.lat = lat;
        this.lng = lng;
    }

    public PloggingChatMessage(PingType pingType, String lat, String lng) {
        this.pingType = pingType;
        this.lat = lat;
        this.lng = lng;
    }

    private PingType pingType;

    private String lat;
    private String lng;
}