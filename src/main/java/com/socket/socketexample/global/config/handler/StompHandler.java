package com.socket.socketexample.global.config.handler;

import com.socket.socketexample.domain.chatting.dto.ChatMessage;
import com.socket.socketexample.domain.chatting.enums.MessageType;
import com.socket.socketexample.domain.chatting.repository.ChatRoomRepository;
import com.socket.socketexample.domain.chatting.service.ChatService;
import com.socket.socketexample.global.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.security.Principal;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Component
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatService chatService;

    private final StringBuffer stringBuffer;

    @Autowired
    public StompHandler(JwtTokenProvider jwtTokenProvider, ChatRoomRepository chatRoomRepository, ChatService chatService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.chatRoomRepository = chatRoomRepository;
        this.chatService = chatService;
        this.stringBuffer = new StringBuffer();
    }

    // websocket을 통해 들어온 요청이 처리 되기전 실행된다.
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT == accessor.getCommand()) { // websocket 연결요청
            String jwtToken = accessor.getFirstNativeHeader("token");
            log.info("CONNECT {}", jwtToken);

            // Header의 jwt token 검증
            jwtTokenProvider.validateToken(jwtToken);

        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) { // 채팅룸 구독요청
            // header정보에서 구독 destination정보를 얻고, roomId를 추출한다.
            String roomId = chatService.getRoomId(Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId"));
            UsernamePasswordAuthenticationToken simpUser = (UsernamePasswordAuthenticationToken)message.getHeaders().get("simpUser");
            String userName = Optional.ofNullable(simpUser.getName()).orElse("no-name");
            // 채팅방에 들어온 클라이언트 sessionId를 roomId와 맵핑해 놓는다.(나중에 특정 세션이 어떤 채팅방에 들어가 있는지 알기 위함)
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            log.info("headers.simpSessionId -> {}", sessionId);
            stringBuffer.append("D:/").append(userName).append("_").append(sessionId).append(".txt");
            // 유저 이름_파일 세션 id로 파일 생성
            chatService.makeFile(stringBuffer.toString());

            chatRoomRepository.setUserEnterInfo(sessionId, roomId);

            // 채팅방의 인원수를 +1한다.
            chatRoomRepository.plusUserCount(roomId);
            // 클라이언트 입장 메시지를 채팅방에 발송한다.(redis publish)
            String name = Optional.ofNullable((Principal) message.getHeaders().get("simpUser")).map(Principal::getName).orElse("UnknownUser");
            chatService.sendChatMessage(ChatMessage.builder().type(MessageType.ENTER).roomId(roomId).sender(name).build());
            stringBuffer.setLength(0);
            log.info("SUBSCRIBED {}, {}", name, roomId);
        } else if (StompCommand.DISCONNECT == accessor.getCommand()) { // Websocket 연결 종료
            // 연결이 종료된 클라이언트 sesssionId로 채팅방 id를 얻는다.
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            String roomId = chatRoomRepository.getUserEnterRoomId(sessionId);
            // 채팅방의 인원수를 -1한다.
            chatRoomRepository.minusUserCount(roomId);
            // 클라이언트 퇴장 메시지를 채팅방에 발송한다.(redis publish)
            String name = Optional.ofNullable((Principal) message.getHeaders().get("simpUser")).map(Principal::getName).orElse("UnknownUser");
            chatService.sendChatMessage(ChatMessage.builder().type(MessageType.QUIT).roomId(roomId).sender(name).build());
            // 퇴장한 클라이언트의 roomId 맵핑 정보를 삭제한다.
            chatRoomRepository.removeUserEnterInfo(sessionId);
            log.info("DISCONNECTED {}, {}", sessionId, roomId);
        } else if (StompCommand.SEND == accessor.getCommand()) {
            log.info("send");
            UsernamePasswordAuthenticationToken simpUser = (UsernamePasswordAuthenticationToken)message.getHeaders().get("simpUser");
            String userName = Optional.ofNullable(simpUser.getName()).orElse("no-name");
            // 채팅방에 들어온 클라이언트 sessionId를 roomId와 맵핑해 놓는다.(나중에 특정 세션이 어떤 채팅방에 들어가 있는지 알기 위함)
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            log.info("headers.simpSessionId -> {}", sessionId);

//            long beforeTime = System.currentTimeMillis(); //코드 실행 전에 시간 받아오기

            //실험할 코드 추가
            stringBuffer.append("D:/").append(userName).append("_").append(sessionId).append(".txt");
            byte[] payload = (byte[])message.getPayload();
            int from = payload.length - 3;
            int to = payload.length - 2;
            while (payload[from] != 34) {
                from--;
            }
            chatService.writeDataInFile(stringBuffer.toString(), copyOfRangeForByte(payload,from, to));

            stringBuffer.setLength(0);
//            long afterTime = System.currentTimeMillis(); // 코드 실행 후에 시간 받아오기
//            long secDiffTime = (afterTime - beforeTime); //두 시간에 차 계산
//            System.out.println(secDiffTime);
        }
        return message;
    }

    public static byte[] copyOfRangeForByte(byte[] original, int from, int to) {
        int newLength = to + 2 - from;
        if (newLength < 0)
            throw new IllegalArgumentException(from + " > " + to);
        byte[] copy = new byte[newLength];
        System.arraycopy(original, from, copy, 0,
                Math.min(original.length - from, newLength - 1));
        copy[copy.length - 1] = 32;
        return copy;
    }
}