package com.socket.socketexample.domain.chatting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginInfo {
    private final String name;
    private final String token;
}