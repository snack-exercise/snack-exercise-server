package com.soma.snackexercise.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Configuration
public class FCMConfig {
    @Value("${fcm.key.path}")
    private String FCM_PRIVATE_KEY_PATH;


    // 메시징만 권한 설정
    @Value("${fcm.key.scope}")
    private String fireBaseScope;

    // project ID
    @Value("${fcm.key.id}")
    private String projectId;

    //fcm 기본 설정 진행
    @Bean
    FirebaseApp firebaseApp() throws IOException {

        InputStream serviceAccountFile = new ClassPathResource("snackpot-fcm.json").getInputStream();

        FirebaseApp firebaseApp = null;
        List<FirebaseApp> firebaseAppList = FirebaseApp.getApps();

        if(firebaseAppList != null && !firebaseAppList.isEmpty()){
            for (FirebaseApp app : firebaseAppList) {
                if(app.getName().equals(FirebaseApp.DEFAULT_APP_NAME)){
                    firebaseApp = app;
                }
            }
        }else{
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccountFile)
                            .createScoped(List.of(fireBaseScope)))
                    .setProjectId(projectId) // 필수는 아님
                    .build();
            firebaseApp = FirebaseApp.initializeApp(options);
        }


        return firebaseApp;
    }

    @Bean
    FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp){
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
