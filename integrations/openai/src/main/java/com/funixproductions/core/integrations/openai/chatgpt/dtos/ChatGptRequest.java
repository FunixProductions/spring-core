package com.funixproductions.core.integrations.openai.chatgpt.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;

@Getter
@AllArgsConstructor
public class ChatGptRequest {

    private final String model;

    private final Collection<Input> input;

    @Getter
    @AllArgsConstructor
    public static class Input {
        private final String role;
        private final String content;
    }

}
