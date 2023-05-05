package com.funixproductions.core.tools.websocket.services.client;

import jakarta.websocket.ClientEndpointConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
class ApiWebSocketClientConfigurator extends ClientEndpointConfig.Configurator {

    private final Map<String, String> headers;

    @Override
    public void beforeRequest(@Nullable final Map<String, List<String>> headers) {
        if (headers != null) {
            for (final Map.Entry<String, String> entry : this.headers.entrySet()) {
                headers.put(entry.getKey(), List.of(entry.getValue()));
            }
        }
    }

}
