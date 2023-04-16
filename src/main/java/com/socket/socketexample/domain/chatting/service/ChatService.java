package com.socket.socketexample.domain.chatting.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socket.socketexample.domain.chatting.domain.ChatRoom;
import com.socket.socketexample.domain.chatting.dto.ChatMessage;
import com.socket.socketexample.domain.chatting.enums.MessageType;
import com.socket.socketexample.domain.chatting.repository.ChatRoomRepository;
import com.socket.socketexample.domain.chatting.request.ChatRoomReq;
import com.socket.socketexample.domain.chatting.response.ChatRoomRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatService {

    private final ChannelTopic channelTopic;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChatRoomRepository chatRoomRepository;

    private final ByteBuffer buffer;

    private static final int bufferSize = 1024 * 1024; // 1MB 버퍼 사이즈

    @Autowired
    public ChatService(ChannelTopic channelTopic, RedisTemplate<String, Object>  redisTemplate, ChatRoomRepository chatRoomRepository) {
        this.channelTopic = channelTopic;
        this.redisTemplate = redisTemplate;
        this.chatRoomRepository = chatRoomRepository;
        this.buffer = ByteBuffer.allocateDirect(bufferSize);
    }

    public void makeFile(String filePath) {
        Path path = Paths.get(filePath);
        boolean exists = Files.exists(path, LinkOption.NOFOLLOW_LINKS);
        if (!exists) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void writeDataInFile(String filePath, byte[] message) {
        Path path = Paths.get(filePath);
        boolean exists = Files.exists(path, LinkOption.NOFOLLOW_LINKS);
        if (exists) {
            try (FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.APPEND)) {
                buffer.put(message);
                buffer.flip();
                fileChannel.write(buffer);
                buffer.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        final Path path = Paths.get(filePath);
//
//        try (final FileChannel srcFileChannel = FileChannel.open(path, StandardOpenOption.READ)) {
//
//        } catch (Exception e) {
//            throw new RuntimeException("File Write 도중 예외 발생", e);
//        }

    }

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

    /**
     * 채팅방에 메시지 발송
     */
    public void sendChatMessage(ChatMessage chatMessage) {
        chatMessage.setUserCount(chatRoomRepository.getUserCount(chatMessage.getRoomId()));
        if (MessageType.ENTER.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getSender() + "님이 방에 입장했습니다.");
            chatMessage.setSender("[알림]");
        } else if (MessageType.QUIT.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getSender() + "님이 방에서 나갔습니다.");
            chatMessage.setSender("[알림]");
        }
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);
    }
}