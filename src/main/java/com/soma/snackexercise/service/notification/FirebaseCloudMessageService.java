package com.soma.snackexercise.service.notification;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FirebaseCloudMessageService {

    @Value("${fcm.key.path}")
    private String FCM_PRIVATE_KEY_PATH;


    // 메시징만 권한 설정
    @Value("${fcm.key.scope}")
    private String fireBaseScope;

     //fcm 기본 설정 진행
//    @PostConstruct
//    public void init() {
//        try {
//            FirebaseOptions options = new FirebaseOptions.Builder()
//                    .setCredentials(
//                            GoogleCredentials
//                                    .fromStream(new ClassPathResource(FCM_PRIVATE_KEY_PATH).getInputStream())
//                                    .createScoped(List.of(fireBaseScope)))
//                    .build();
//
//            if (FirebaseApp.getApps().isEmpty()) {
//                FirebaseApp.initializeApp(options);
//                log.info("Firebase application has been initialized");
//            }
//        } catch (IOException e) {
//            log.error(e.getMessage());
//            e.printStackTrace();
//            // spring 뜰때 알림 서버가 잘 동작하지 않는 것이므로 바로 죽임
//            throw new RuntimeException(e.getMessage());
//        }
//    }



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
            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(multicastMessage);
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
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            log.info("FirebaseMessagingException: {}, fail token: {}", e.getMessage(), token);
        }
    }
}
