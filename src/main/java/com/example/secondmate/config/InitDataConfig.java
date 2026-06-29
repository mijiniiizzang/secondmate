package com.example.secondmate.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.secondmate.service.UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class InitDataConfig {
    private final UserService userService;

    @Bean
    public CommandLineRunner init() {
        return args -> {
            userService.createAdmin();
        };
    }
}
