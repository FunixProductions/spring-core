package com.funixproductions.core.tools.websocket.services.client;

import com.funixproductions.core.exceptions.ApiException;
import jakarta.websocket.*;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

@Slf4j
public abstract class ApiWebSocketClientHandler extends Endpoint {

    private final URI uri;
    private final ClientEndpointConfig config;
    private final WebSocketContainer container;
    private final MessageHandler messageHandler;

    private boolean running = true;
    private Session session;

    protected ApiWebSocketClientHandler(@NonNull final String uriWs,
                                     @Nullable final Map<String, String> headers) throws ApiException {
        try {
            this.uri = new URI(uriWs);
            this.container = ContainerProvider.getWebSocketContainer();
            this.messageHandler = new ApiWebSocketClientMessageHandler(this);
            this.config = ClientEndpointConfig.Builder.create()
                    .configurator(new ApiWebSocketClientConfigurator(headers))
                    .build();

            reconnect();
        } catch (URISyntaxException uriSyntaxException) {
            throw new ApiException(String.format("L'uri est invalide: %s", uriWs), uriSyntaxException);
        }
    }

    @Override
    public final void onError(Session session, Throwable throwable) {
        log.error("Erreur ws endpoint {}", this.uri, throwable);
    }

    @Override
    public final void onOpen(Session session, EndpointConfig endpointConfig) {
        log.info("Connected to ws enpoint {}.", this.uri);
        session.addMessageHandler(this.messageHandler);
        this.session = session;
    }

    @Override
    public final void onClose(Session session, CloseReason closeReason) {
        log.info("Ws session closed {} reason {}", this.uri, closeReason);
        reconnect();
    }

    public final void sendMessage(final String message) throws ApiException {
        if (this.running && this.session != null && this.session.isOpen()) {
            this.session.getAsyncRemote().sendText(message);
        } else {
            throw new ApiException("Client not available.");
        }
    }

    public abstract void onMessage(final String message) throws ApiException;

    public final void stopWebsocket() throws ApiException {
        this.running = false;
        disconnect();
    }

    private void reconnect() throws ApiException {
        disconnect();

        if (running) {
            try (final Session newSession = this.container.connectToServer(this, this.config, this.uri)) {
                this.session = newSession;
            } catch (IOException | DeploymentException e) {
                throw new ApiException(String.format("Connexion impossible au websocket uri: %s", this.uri), e);
            }
        }
    }

    private void disconnect() throws ApiException {
        try {
            if (session != null && session.isOpen()) {
                session.close();
                session = null;
            }
        } catch (IOException e) {
            throw new ApiException(String.format("Impossible de fermer le client ws uri: %s", this.uri));
        }
    }

}
