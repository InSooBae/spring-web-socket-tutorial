package com.socket.socketexample.global.util;

public class Utils {
    public static String getUriWithoutRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1)
            return destination.substring(10, lastIndex);
        else
            return "";
    }
}
