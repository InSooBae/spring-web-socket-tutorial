package com.socket.socketexample.domain.chatting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socket.socketexample.domain.chatting.domain.ChatRoom;
import com.socket.socketexample.domain.chatting.response.ChatMessageRes;
import com.socket.socketexample.domain.chatting.response.ChatRoomRes;
import com.socket.socketexample.domain.chatting.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketChatHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final ChatService chatService;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("payload = {}", payload);

        ChatMessageRes chatMessageRes = objectMapper.readValue(payload, ChatMessageRes.class);
        ChatRoom room = chatService.findRoomById(chatMessageRes.getRoomId());
        room.handleActions(session, chatMessageRes, chatService);
    }
}
