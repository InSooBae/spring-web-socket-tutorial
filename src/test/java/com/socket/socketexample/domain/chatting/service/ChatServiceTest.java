package com.socket.socketexample.domain.chatting.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ChatServiceTest {

    @Autowired
    ChatService chatService;
    @Test
    void writeDataInFile() throws InterruptedException {
        AtomicInteger successCount = new AtomicInteger();
        int numberOfExcute = 7;
        ExecutorService service = Executors.newFixedThreadPool(7);
        CountDownLatch latch = new CountDownLatch(numberOfExcute);

        // when
        for (int i = 0; i < numberOfExcute; i++) {
            int finalI = i;
            service.execute(() -> {
                try {
                    chatService.writeDataInFile("D:/qospwmf_c1bnpyjp.txt", new byte[] {(byte) finalI});
                    System.out.println("TestId : " + finalI);
                    successCount.getAndIncrement();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                latch.countDown();
            });
        }
        latch.await();
    }

    @Test
    void stringBufferSync() throws InterruptedException {
        AtomicInteger successCount = new AtomicInteger();
        StringBuilder sb = new StringBuilder();
        int numberOfExcute = 7;
        ExecutorService service = Executors.newFixedThreadPool(7);
        CountDownLatch latch = new CountDownLatch(numberOfExcute);

        // when
        for (int i = 0; i < numberOfExcute; i++) {
            int finalI = i;
            service.execute(() -> {
                try {
                    String name;
                    synchronized (this) {
                        name = sb.append("1").append("2").append("3").toString();
                        sb.setLength(0);
                    }
                    System.out.println("TestId : " + finalI);
                    System.out.println(name);
                    successCount.getAndIncrement();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                latch.countDown();
            });
        }
        latch.await();

        System.out.println(sb);
    }
}