package com.funixproductions.core.tools.websocket.services;

import com.funixproductions.core.exceptions.ApiBadRequestException;
import com.funixproductions.core.exceptions.ApiException;
import com.funixproductions.core.tools.websocket.dtos.WebSocketPingMessageRequest;
import lombok.NonNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

public abstract class ApiWebsocketServerHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> webSocketSessions = new HashMap<>();
    private final Map<String, WebSocketPingMessageRequest> sessionsPings = new HashMap<>();

    public final void broadcastMessage(final String message) throws ApiException {
        try {
            final TextMessage textMessage = new TextMessage(message);

            for (final WebSocketSession session : webSocketSessions.values()) {
                if (session != null && session.isOpen()) {
                    session.sendMessage(textMessage);
                }
            }
        } catch (IOException ioException) {
            throw new ApiException("Impossible d'envoyer un message ws.", ioException);
        }
    }

    public final void sendMessageToSessionId(final String sessionId, final String message) throws ApiException {
        try {
            final TextMessage textMessage = new TextMessage(message);
            final WebSocketSession session = webSocketSessions.get(sessionId);

            if (session != null) {
                session.sendMessage(textMessage);
            } else {
                throw new ApiBadRequestException(String.format("La session ws id %s n'existe pas.", sessionId));
            }
        } catch (IOException ioException) {
            throw new ApiException("Impossible d'envoyer un message ws.", ioException);
        }
    }

    public final Map<String, WebSocketSession> getSessions() {
        return this.webSocketSessions;
    }

    @Override
    public final void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        webSocketSessions.put(session.getId(), session);
    }

    @Override
    public final void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
        if (session.isOpen()) {
            session.close();
        }
        this.webSocketSessions.remove(session.getId());
        this.onClientDisconnect(session.getId());
    }

    @Override
    protected final void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
        final String messageStr = message.getPayload();

        if (messageStr.startsWith("pong")) {
            this.newPongMessage(session, messageStr);
        } else {
            this.newWebsocketMessage(session, messageStr);
        }
    }

    @Scheduled(fixedRate = 20, timeUnit = TimeUnit.SECONDS)
    public final void sendPingRequestsAndCheckZombies() {
        disconnectAllZombiesClients();
        sendPingRequests();
    }

    private void disconnectAllZombiesClients() throws ApiException {
        final Instant now = Instant.now();
        final Set<String> sessionsToRemove = new HashSet<>();
        final Set<String> pingsToRemove = new HashSet<>();

        for (final Map.Entry<String, WebSocketPingMessageRequest> ping : sessionsPings.entrySet()) {
            final String sessionId = ping.getKey();
            final WebSocketSession session = webSocketSessions.get(sessionId);
            if (session == null) {
                pingsToRemove.add(sessionId);
                continue;
            }

            final WebSocketPingMessageRequest validationSession = ping.getValue();
            if (validationSession.getSentAt().plus(1, ChronoUnit.MINUTES).isBefore(now)) {
                sessionsToRemove.add(sessionId);
            }
        }

        for (final String sessionId : pingsToRemove) {
            this.sessionsPings.remove(sessionId);
        }

        for (final String sessionId : sessionsToRemove) {
            final WebSocketSession session = webSocketSessions.get(sessionId);

            if (session != null) {
                try {
                    session.close();
                    this.sessionsPings.remove(sessionId);
                    this.webSocketSessions.remove(sessionId);
                    onClientDisconnect(sessionId);
                } catch (IOException e) {
                    throw new ApiException(String.format("Impossible de fermer la session ws lors du clean des zombies. Session id %s.", session.getId()), e);
                }
            }
        }
    }

    private void sendPingRequests() throws ApiException {
        for (final WebSocketSession session : this.webSocketSessions.values()) {
            if (session == null || !session.isOpen()) {
                continue;
            }

            final String sessionId = session.getId();
            this.sessionsPings.computeIfAbsent(sessionId, id -> {
                final WebSocketPingMessageRequest pingMessageRequest = new WebSocketPingMessageRequest();

                this.sendMessageToSessionId(id, String.format("ping:%s", pingMessageRequest.getVerification()));
                return pingMessageRequest;
            });
        }
    }

    private void newPongMessage(@NonNull WebSocketSession session, @NonNull String pong) {
        final String[] pongData = pong.split(":");

        if (pongData.length == 2) {
            final String stringValidation = pongData[1];
            final WebSocketPingMessageRequest validation = this.sessionsPings.get(session.getId());

            if (validation != null && validation.getVerification().equals(stringValidation)) {
                this.sessionsPings.remove(session.getId());
            }
        }
    }

    protected abstract void newWebsocketMessage(@NonNull WebSocketSession session, @NonNull String message) throws Exception;

    protected void onClientDisconnect(final String sessionId) {}

    @Override
    public final void handleMessage(@NonNull WebSocketSession session, @NonNull WebSocketMessage<?> message) throws Exception {
        super.handleMessage(session, message);
    }

    @Override
    protected final void handlePongMessage(@NonNull WebSocketSession session, @NonNull PongMessage message) throws Exception {
        super.handlePongMessage(session, message);
    }

    @Override
    public final void handleTransportError(@NonNull WebSocketSession session, @NonNull Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
    }

    @Override
    public final boolean supportsPartialMessages() {
        return super.supportsPartialMessages();
    }

    @Override
    protected final void handleBinaryMessage(@NonNull WebSocketSession session, @NonNull BinaryMessage message) {
        super.handleBinaryMessage(session, message);
    }
}
