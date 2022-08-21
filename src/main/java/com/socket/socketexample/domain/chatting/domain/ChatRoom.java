package com.socket.socketexample.domain.chatting.domain;

import com.socket.socketexample.domain.chatting.request.ChatRoomReq;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Builder
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoom implements Serializable {

    private static final long serialVersionUID = 8106356809648006881L;

    private final String roomId;
    private final String name;

    public static ChatRoom create(String roomName) {
        return new ChatRoom(randomUUIDToString(), roomName);
    }

    private static String randomUUIDToString() {
        return UUID.randomUUID().toString();
    }
}
