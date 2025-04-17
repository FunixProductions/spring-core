package com.funixproductions.core.integrations.openai.chatgpt.services;

import com.funixproductions.core.exceptions.ApiException;
import com.funixproductions.core.integrations.openai.chatgpt.TestApp;
import com.funixproductions.core.integrations.openai.chatgpt.clients.ChatGptClient;
import com.funixproductions.core.integrations.openai.chatgpt.dtos.ChatGptRequest;
import com.funixproductions.core.integrations.openai.chatgpt.dtos.ChatGptResponse;
import com.funixproductions.core.integrations.openai.chatgpt.enums.ChatGptModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(
        classes = {
                TestApp.class
        }
)
class ChatGptServiceTest {

    @MockitoBean
    private ChatGptClient chatGptClient;

    @Autowired
    private ChatGptService chatGptService;

    @Test
    void testService() throws ApiException {
        final String responseText = "Hello, world!";
        final ChatGptResponse response = new ChatGptResponse();

        response.setOutput(
                List.of(
                        new ChatGptResponse.Output(
                                List.of(
                                        new ChatGptResponse.Content(responseText)
                                )
                        )
                )
        );

        when(chatGptClient.sendRequest(any(ChatGptRequest.class), any(String.class))).thenReturn(response);

        String result = chatGptService.sendGptRequest(
                ChatGptModel.GPT_3_5_TURBO,
                "Developer prompt",
                "User prompt"
        );

        assertEquals(responseText, result);

        result = chatGptService.sendGptRequest(
                ChatGptModel.GPT_3_5_TURBO,
                null,
                "User prompt"
        );

        assertEquals(responseText, result);
    }

}
