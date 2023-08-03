package com.soma.snackexercise;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.TimeZone;

@Slf4j
@EnableScheduling
@OpenAPIDefinition(servers = {@Server(url = "/", description = "https://dev-api.snackexercise.com")})
@EnableJpaAuditing
@SpringBootApplication
public class SnackExerciseApplication {

    @PostConstruct
    public void started(){
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        log.info("현재시각 : " + new Date());
    }

    public static void main(String[] args) {
        SpringApplication.run(SnackExerciseApplication.class, args);
    }

}
