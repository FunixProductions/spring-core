package com.funixproductions.core.tools.websocket.services.client;

import com.funixproductions.core.exceptions.ApiException;
import jakarta.websocket.MessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
class ApiWebSocketClientMessageHandler implements MessageHandler.Whole<String> {

    private final ApiWebSocketClientHandler handler;

    @Override
    public void onMessage(String message) {
        if (message.startsWith("ping")) {
            handlePing(message);
        } else {
            try {
                this.handler.onMessage(message);
            } catch (ApiException e) {
                log.error("ws new message erreur.", e);
            }
        }
    }

    private void handlePing(final String message) {
        final String[] data = message.split(":");

        if (data.length == 2) {
            try {
                this.handler.sendMessage(String.format("pong:%s", data[1]));
            } catch (ApiException e) {
                log.error("Erreur lors de la réponse ping,pong ws.", e);
            }
        }
    }
}
