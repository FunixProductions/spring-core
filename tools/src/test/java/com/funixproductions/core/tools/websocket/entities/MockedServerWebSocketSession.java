package com.funixproductions.core.tools.websocket.entities;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketExtension;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.util.*;

@RequiredArgsConstructor
public class MockedServerWebSocketSession implements WebSocketSession {

    private final WebSocketServer webSocketTest;
    private final String id = UUID.randomUUID().toString();

    @Override
    public @NonNull String getId() {
        return id;
    }

    @Override
    public URI getUri() {
        return null;
    }

    @Override
    public @NonNull HttpHeaders getHandshakeHeaders() {
        return new HttpHeaders();
    }

    @Override
    public @NonNull Map<String, Object> getAttributes() {
        return new HashMap<>();
    }

    @Override
    public Principal getPrincipal() {
        return null;
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return null;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return null;
    }

    @Override
    public String getAcceptedProtocol() {
        return null;
    }

    @Override
    public void setTextMessageSizeLimit(int messageSizeLimit) {

    }

    @Override
    public int getTextMessageSizeLimit() {
        return 0;
    }

    @Override
    public void setBinaryMessageSizeLimit(int messageSizeLimit) {

    }

    @Override
    public int getBinaryMessageSizeLimit() {
        return 0;
    }

    @Override
    public @NonNull List<WebSocketExtension> getExtensions() {
        return new ArrayList<>();
    }

    @Override
    public void sendMessage(@NonNull WebSocketMessage<?> message) throws IOException {
        try {
            webSocketTest.newWebsocketMessage(this, (String) message.getPayload());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void close(@NonNull CloseStatus status) throws IOException {
    }

}
