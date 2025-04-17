package com.funixproductions.core.integrations.openai.chatgpt.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.NoSuchElementException;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatGptResponse {

    private List<Output> output;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Output {
        private List<Content> content;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Content {
        private String text;
    }

    @Nullable
    public String getText() {
        try {
            if (this.output != null) {
                final Output output = this.output.getFirst();

                if (output != null && output.getContent() != null) {
                    final Content content = output.getContent().getFirst();

                    if (content != null) {
                        return content.getText();
                    }
                }
            }

            return null;
        } catch (NoSuchElementException e) {
            return null;
        }
    }

}
