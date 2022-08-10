package com.socket.socketexample.domain.chatting.domain;

import com.socket.socketexample.domain.chatting.request.ChatRoomReq;
import com.socket.socketexample.domain.chatting.response.ChatMessageRes;
import com.socket.socketexample.domain.chatting.enums.MessageType;
import com.socket.socketexample.domain.chatting.service.ChatService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Builder
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoom {

    private final String roomId;
    private final String name;

    private final Set<WebSocketSession> socketSessions = new HashSet<>();

    public void handleActions(WebSocketSession session, ChatMessageRes chatMessage, ChatService chatService) {
        if (chatMessage.getType().equals(MessageType.ENTER)) {
            socketSessions.add(session);
            chatMessage.setMessage(chatMessage.getSender() + "님이 입장했습니다.");
        }
        sendMessage(chatMessage, chatService);
    }

    private  <T> void sendMessage(T message, ChatService chatService) {
        socketSessions.parallelStream().forEach(session -> chatService.sendMessage(session, message));
    }

    public static ChatRoom create(ChatRoomReq chatRoomReq) {
        return new ChatRoom(randomUUIDToString(), chatRoomReq.getRoomTitle());
    }

    private static String randomUUIDToString() {
        return UUID.randomUUID().toString();
    }
}
