package com.soma.snackexercise.service.notification;


import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class FirebaseCloudMessageService {
    private final FirebaseMessaging firebaseMessaging;

    /**
     * tokenList를 받아 여러 개의 기기로 알림 보내기
     * @param tokenList 알림 보낼 fcmToken 리스트
     * @param title 알림 메세지 제목
     * @param body 알림 메세지 본문
     */
    public void sendByTokenList(List<String> tokenList, String title, String body) {

        //여러기기에 메세지 전송
        MulticastMessage multicastMessage = MulticastMessage.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .addAllTokens(tokenList)
                .build();

        try {
            BatchResponse response = firebaseMessaging.sendMulticast(multicastMessage);
            if (response.getFailureCount() > 0) {
                List<SendResponse> responses = response.getResponses();
                List<String> failedTokens = new ArrayList<>();
                for (int i = 0; i < responses.size(); i++) {
                    if (!responses.get(i).isSuccessful()) {
                        failedTokens.add(tokenList.get(i));
                    }
                }
                log.info("List of tokens that caused failures: {}", failedTokens);
            }
        } catch (FirebaseMessagingException e) {
            log.info("FirebaseMessagingException: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 하나의 token을 받아서, 한개의 기기로 알림 보내기
     * @param token 알림 보낼 fcmToken
     * @param title 알림 메세지 제목
     * @param body 알림 메세지 본문
     */
    public void sendByToken(String token, String title, String body) {

        // 메세지 만들기
        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();

        try {
            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            log.info("FirebaseMessagingException: {}, fail token: {}", e.getMessage(), token);
        }
    }
}
