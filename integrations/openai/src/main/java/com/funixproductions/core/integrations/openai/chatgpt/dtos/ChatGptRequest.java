package com.funixproductions.core.integrations.openai.chatgpt.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;

@Getter
@AllArgsConstructor
public class ChatGptRequest {

    private final String model;

    private final Collection<Input> input;

    @JsonProperty(value = "max_tokens")
    private final Integer maxTokens;

    @Getter
    @AllArgsConstructor
    public static class Input {
        private final String role;
        private final String content;
    }

}
