package com.funixproductions.core.integrations.openai.chatgpt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication(scanBasePackages = "com.funixproductions")
@EnableFeignClients(basePackages = "com.funixproductions")
@EnableSpringDataWebSupport
public class TestApp {

    public static void main(String[] args) {
        SpringApplication.run(TestApp.class);
    }

}
