package com.soma.snackexercise;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@OpenAPIDefinition(servers = {@Server(url = "/", description = "https://dev-api.snackexercise.com")})
@EnableJpaAuditing
@SpringBootApplication
public class SnackExerciseApplication {

    public static void main(String[] args) {
        SpringApplication.run(SnackExerciseApplication.class, args);
    }

}
