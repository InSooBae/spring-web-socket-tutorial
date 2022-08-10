package com.socket.socketexample.domain.chatting.response;

import com.socket.socketexample.domain.chatting.domain.ChatRoom;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChatRoomRes {

    private String roomId;
    private String roomTitle;

    public static ChatRoomRes of(ChatRoom chatRoom) {
        return ChatRoomRes
                .builder()
                .roomId(chatRoom.getRoomId())
                .roomTitle(chatRoom.getName())
                .build();
    }
}
