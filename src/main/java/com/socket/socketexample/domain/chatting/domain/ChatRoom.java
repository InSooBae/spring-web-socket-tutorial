package com.socket.socketexample.domain.chatting.domain;

import com.socket.socketexample.domain.chatting.request.ChatRoomReq;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoom implements Serializable {

    private static final long serialVersionUID = 6494678977089006639L;

    private String roomId;
    private String name;
    private long userCount; // 채팅방 인원수

    public static ChatRoom create(String name) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomId = UUID.randomUUID().toString();
        chatRoom.name = name;
        return chatRoom;
    }
}