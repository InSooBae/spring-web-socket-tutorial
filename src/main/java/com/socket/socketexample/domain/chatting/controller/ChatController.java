package com.socket.socketexample.domain.chatting.controller;

import com.socket.socketexample.domain.chatting.enums.MessageType;
import com.socket.socketexample.domain.chatting.request.ChatMessageReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
@Slf4j
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/chat/message")
    public void createRoom(ChatMessageReq chatMessageReq) {
        if (MessageType.ENTER.equals(chatMessageReq.getType()))
            chatMessageReq.setMessage(chatMessageReq.getSender() + "님이 입장하셨습니다.");
        messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessageReq.getRoomId(), chatMessageReq);
    }

}
