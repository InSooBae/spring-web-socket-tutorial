package com.socket.socketexample.domain.chatting.controller;

import com.socket.socketexample.domain.chatting.domain.ChatRoom;
import com.socket.socketexample.domain.chatting.request.ChatRoomReq;
import com.socket.socketexample.domain.chatting.response.ChatRoomRes;
import com.socket.socketexample.domain.chatting.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ChatRoomRes createRoom(@RequestBody ChatRoomReq chatRoomReq) {
        log.info("ChatController - createRoom - chatRoomReq.roomTitle: {}", chatRoomReq.getRoomTitle());
        return chatService.createRoom(chatRoomReq);
    }

    @GetMapping
    public List<ChatRoomRes> findAllRoom() {
        return chatService.findAllRoom();
    }
}