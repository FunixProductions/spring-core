package com.funixproductions.core.integrations.openai.chatgpt.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "funixproductions.openai.chatgpt")
public class ChatGptConfig {

    /**
     * The OpenAI API key.
     */
    private String apiKey;

}
