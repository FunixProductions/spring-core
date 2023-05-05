package com.funixproductions.core.tools.websocket.services;

import com.funixproductions.core.tools.websocket.dtos.WebSocketPingMessageRequest;
import com.funixproductions.core.tools.websocket.entities.MockedServerWebSocketSession;
import com.funixproductions.core.tools.websocket.entities.WebSocketServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApiWebSocketHandlerServerTest {

    private final WebSocketServer webSocketTest = new WebSocketServer();

    @BeforeEach
    void setupSessions() throws Exception {
        webSocketTest.afterConnectionEstablished(generateSession());
        webSocketTest.afterConnectionEstablished(generateSession());
    }

    @Test
    void sendMessageTest() throws Exception {
        final String message = "Je suis donc un message";
        webSocketTest.handleTextMessage(generateSession(), new TextMessage(message));
        assertEquals(message, webSocketTest.getLastMessage());
        webSocketTest.handleTextMessage(generateSession(), new TextMessage("pong:blabla"));
        assertEquals(message, webSocketTest.getLastMessage());
    }

    @Test
    void sendPingRequestTest() throws Exception {
        final WebSocketSession session = generateSession();
        webSocketTest.afterConnectionEstablished(session);
        webSocketTest.sendPingRequestsAndCheckZombies();

        final Field field = webSocketTest.getClass().getSuperclass().getDeclaredField("sessionsPings");
        field.setAccessible(true);
        final Map<String, WebSocketPingMessageRequest> pings = (HashMap<String, WebSocketPingMessageRequest>) field.get(webSocketTest);
        assertTrue(pings.containsKey(session.getId()));
        field.setAccessible(false);
    }

    @Test
    void broadcastMessageTest() throws Exception {
        final String message = "test bc";
        webSocketTest.broadcastMessage(message);
        assertEquals(message, webSocketTest.getLastMessage());
    }

    @Test
    void sendMessageToSpecificClient() throws Exception {
        final String message = "test to someone";
        final WebSocketSession session = generateSession();
        webSocketTest.afterConnectionEstablished(session);

        webSocketTest.sendMessageToSessionId(session.getId(), message);
        assertEquals(message, webSocketTest.getLastMessage());
    }

    private WebSocketSession generateSession() {
        return new MockedServerWebSocketSession(this.webSocketTest);
    }

}
