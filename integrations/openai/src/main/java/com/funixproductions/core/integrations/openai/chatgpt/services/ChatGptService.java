package com.funixproductions.core.integrations.openai.chatgpt.services;

import com.funixproductions.core.exceptions.ApiException;
import com.funixproductions.core.integrations.openai.chatgpt.clients.ChatGptClient;
import com.funixproductions.core.integrations.openai.chatgpt.configs.ChatGptConfig;
import com.funixproductions.core.integrations.openai.chatgpt.dtos.ChatGptRequest;
import com.funixproductions.core.integrations.openai.chatgpt.dtos.ChatGptResponse;
import com.funixproductions.core.integrations.openai.chatgpt.enums.ChatGptModel;
import com.google.common.base.Strings;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatGptService {

    private final ChatGptClient chatGptClient;
    private final ChatGptConfig chatGptConfig;

    /**
     * Send a request to the OpenAI API.
     * @param model the model to use for the request.
     * @param devPrompt developer messages are instructions provided by the application developer, prioritized ahead of user messages.
     * @param prompt user messages are instructions provided by an end user, prioritized behind developer messages.
     * @return the response from the OpenAI API.
     */
    public String sendGptRequest(
            final @NonNull ChatGptModel model,
            final @Nullable String devPrompt,
            final @NonNull String prompt
    ) throws ApiException {
        try {
            final List<ChatGptRequest.Input> inputs = new ArrayList<>();

            inputs.add(new ChatGptRequest.Input("user", prompt));
            if (!Strings.isNullOrEmpty(devPrompt)) {
                inputs.add(new ChatGptRequest.Input("developer", devPrompt));
            }

            final ChatGptResponse response = this.chatGptClient.sendRequest(
                    new ChatGptRequest(model.getModel(), inputs),
                    "Bearer " + this.chatGptConfig.getApiKey()
            );
            final String text = response.getText();

            if (Strings.isNullOrEmpty(text)) {
                throw new ApiException("Failed to get response from OpenAI API, empty response");
            } else {
                return text;
            }
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Failed to send request to OpenAI API", e);
        }
    }

}
