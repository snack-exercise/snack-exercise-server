package com.soma.snackexercise.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class HealthController {
    @GetMapping("/health")
    public String healthCheck(){
        return "health";
    }
}
