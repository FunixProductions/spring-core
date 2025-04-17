package com.funixproductions.core.integrations.openai.chatgpt.clients;

import com.funixproductions.core.integrations.openai.chatgpt.dtos.ChatGptRequest;
import com.funixproductions.core.integrations.openai.chatgpt.dtos.ChatGptResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "chat-gpt-client",
        url = "https://api.openai.com/v1/responses"
)
public interface ChatGptClient {

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ChatGptResponse sendRequest(
            @RequestBody ChatGptRequest request,
            @RequestHeader("Authorization") String token
    );

}
