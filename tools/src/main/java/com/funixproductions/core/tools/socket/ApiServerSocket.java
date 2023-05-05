package com.funixproductions.core.tools.socket;

import com.funixproductions.core.exceptions.ApiException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

@Slf4j(topic = "ApiServerSocket")
public abstract class ApiServerSocket {

    private final ServerSocket serverSocket;
    private final Set<Socket> clients = new HashSet<>();

    protected ApiServerSocket(final int port) throws ApiException {
        try {
            this.serverSocket = new ServerSocket(port);
            new Thread(this::worker).start();
        } catch (IOException e) {
            throw new ApiException("Une erreur est surveue lors de la création du socket serveur.", e);
        }
    }

    public final void closeServer() throws ApiException {
        try {
            for (final Socket socket : clients) {
                if (!socket.isClosed()) {
                    socket.close();
                }
            }
        } catch (IOException e) {
            throw new ApiException("Une erreur est survenue lors de la fermeture du socket client.", e);
        }

        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new ApiException("Une erreur est survenue lors de la fermeture du socket serveur.", e);
        }
    }

    private void worker() {
        while (!this.serverSocket.isClosed()) {
            try {
                final Socket socket = this.serverSocket.accept();

                this.newClient(socket);
                this.clients.add(socket);
                clients.removeIf(Socket::isClosed);
            } catch (IOException e) {
                if (!this.serverSocket.isClosed()) {
                    log.error("Une erreur est survenue lors de l'ajout d'un nouveau client.", e);
                }
            }
        }
    }

    public abstract void newClient(final Socket socket);
}
