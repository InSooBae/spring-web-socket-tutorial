package com.socket.socketexample.domain.chatting.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socket.socketexample.domain.chatting.domain.ChatRoom;
import com.socket.socketexample.domain.chatting.request.ChatRoomReq;
import com.socket.socketexample.domain.chatting.response.ChatRoomRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {

    private final ObjectMapper objectMapper;
    private Map<String, ChatRoom> chatRoomMap;

    @PostConstruct
    private void init() {
        chatRoomMap = new LinkedHashMap<>();
    }

    public List<ChatRoomRes> findAllRoom() {
        return chatRoomMap.values().stream().map(ChatRoomRes::of).collect(Collectors.toList());
    }

    public ChatRoom findRoomById(String roomId) {
        return chatRoomMap.get(roomId);
    }

    public ChatRoomRes createRoom(ChatRoomReq chatRoomReq) {
        ChatRoom chatRoom = ChatRoom.create(chatRoomReq);
        chatRoomMap.put(chatRoom.getRoomId(), chatRoom);
        return ChatRoomRes.of(chatRoom);
    }

    public <T> void sendMessage(WebSocketSession session, T message) {
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}