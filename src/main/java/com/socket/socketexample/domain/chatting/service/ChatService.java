package com.socket.socketexample.domain.chatting.service;

import com.socket.socketexample.domain.chatting.dto.ChatMessage;
import com.socket.socketexample.domain.chatting.dto.PloggingChatMessage;
import com.socket.socketexample.domain.chatting.enums.MessageType;
import com.socket.socketexample.domain.chatting.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.util.StringTokenizer;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChannelTopic crewTopic;

    private final ChannelTopic ploggingTopic;
    private final RedisTemplate redisTemplate;
    private final ChatRoomRepository chatRoomRepository;

    /**
     * destination정보에서 roomId 추출
     */
    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1)
            return destination.substring(lastIndex + 1);
        else
            return "";
    }

    public String getUriWithoutRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1)
            return destination.substring(10, lastIndex);
        else
            return "";
    }

    /**
     * 채팅방에 메시지 발송
     */
    public void sendChatMessage(ChatMessage chatMessage) {
        String topic = crewTopic.getTopic();
        PloggingChatMessage ploggingChatMessage = null;
        boolean isPloggingMessage = canDownCastingChatMessageToPloggingMessage(chatMessage);
        if (isPloggingMessage) {
            ploggingChatMessage = (PloggingChatMessage) chatMessage;
            topic = ploggingTopic.getTopic();
        }
        chatMessage.setUserCount(chatRoomRepository.getUserCount(chatMessage.getRoomId()));
        if (MessageType.ENTER.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getSender() + "님이 방에 입장했습니다.");
            chatMessage.setSender("[알림]");
        } else if (MessageType.QUIT.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getSender() + "님이 방에서 나갔습니다.");
            chatMessage.setSender("[알림]");
        } else if (MessageType.PING.equals(chatMessage.getType())) {
            // 핑

        } else if (MessageType.POS.equals(chatMessage.getType())) {
            // 사람 좌표
        }

        if (isPloggingMessage) {
            redisTemplate.convertAndSend(topic, ploggingChatMessage);
        } else {
            redisTemplate.convertAndSend(topic, chatMessage);
        }
    }

    private static boolean canDownCastingChatMessageToPloggingMessage(ChatMessage chatMessage) {
        return chatMessage instanceof PloggingChatMessage;
    }

}