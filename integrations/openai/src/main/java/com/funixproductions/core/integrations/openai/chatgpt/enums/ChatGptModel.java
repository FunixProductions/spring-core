package com.funixproductions.core.integrations.openai.chatgpt.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatGptModel {
    /**
     * <a href="https://platform.openai.com/docs/models/o4-mini">o4-mini</a>
     */
    GPT_o4_MINI("o4-mini"),

    /**
     * <a href="https://platform.openai.com/docs/models/gpt-4.1">Flagship GPT model for complex tasks</a>
     */
    GPT_4_1("gpt-4.1"),

    /**
     * <a href="https://platform.openai.com/docs/models/gpt-4o">Fast, intelligent, flexible GPT model</a>
     */
    GPT_4o("gpt-4o"),

    /**
     * <a href="https://platform.openai.com/docs/models/gpt-3.5-turbo">Legacy GPT model for cheaper chat and non-chat tasks</a>
     */
    GPT_3_5_TURBO("gpt-3.5-turbo");

    private final String model;

}
