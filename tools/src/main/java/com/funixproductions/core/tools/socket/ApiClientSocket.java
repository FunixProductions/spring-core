package com.funixproductions.core.tools.socket;

import com.funixproductions.core.exceptions.ApiException;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Class used to manage client sockets
 */
@Slf4j(topic = "ApiClientSocket")
public abstract class ApiClientSocket {

    private final Queue<String> messageQueue = new LinkedList<>();
    private final AtomicBoolean running = new AtomicBoolean(true);

    private final String socketAddress;
    private final int port;
    private final boolean isServerSocket;

    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;

    private int cooldownMessages = 0;

    /**
     * Used to connect to a server
     * @param socketAddress server address
     * @param port server port
     * @throws ApiException when auth exception
     */
    protected ApiClientSocket(final String socketAddress, final int port) throws ApiException {
        this.socketAddress = socketAddress;
        this.port = port;
        this.isServerSocket = false;

        try {
            this.socket = new Socket(socketAddress, port);
            new Thread(this::worker).start();
        } catch (IOException e) {
            throw new ApiException("Erreur lors de l'initialisation du socket client.", e);
        }
    }

    /**
     * Used on a server socket when a new connection appears
     * @param socket socket given from server socket
     * @throws ApiException when error occurs with socket
     */
    protected ApiClientSocket(final Socket socket) throws ApiException {
        this.socketAddress = "";
        this.port = 0;
        this.isServerSocket = true;

        this.socket = socket;
        new Thread(this::worker).start();
    }

    /**
     * Send a message to the server where the socket is connected to (queue)
     * @param message message, the program adds BREAK_LINE automatically
     */
    public final void sendMessage(final String message) {
        if (!Strings.isNullOrEmpty(message)) {
            this.messageQueue.add(message);
        }
    }

    /**
     * Close the connection with the server
     */
    public final void close() {
        try {
            this.running.set(false);

            if (this.writer != null) {
                this.writer.close();
                this.writer = null;
            }

            if (this.reader != null) {
                this.reader.close();
                this.reader = null;
            }

            if (this.socket != null && !this.socket.isClosed()) {
                this.socket.close();
            }
        } catch (IOException e) {
            log.error("Error occurred while clearing socket.", e);
        }
    }

    /**
     * Worker method where the socket do its actions
     */
    private void worker() {
        new Thread(this::runMessagePool).start();

        while (this.running.get()) {

            try {
                this.writer = new PrintWriter(this.socket.getOutputStream(), true);
                this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                runSocket();

                log.info("Socket client: connexion lost host: {} port: {}.", this.socket.getInetAddress(), this.socket.getPort());
                close();
                if (!isServerSocket) {
                    running.set(true);
                    log.info("Initiating a new connection...");
                    socket = new Socket(this.socketAddress, this.port);
                    log.info("Socket connected to {}:{} !", socketAddress, port);
                }
            } catch (IOException e) {
                log.error("Une erreur est survenue lors de la connexion au socket. Erreur: {} Host: {} Port: {}", e.getMessage(), this.socket.getInetAddress(), this.socket.getPort());

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                }
            } finally {
                try {
                    if (this.writer != null) {
                        this.writer.close();
                        this.writer = null;
                    }

                    if (this.reader != null) {
                        this.reader.close();
                        this.reader = null;
                    }
                } catch (IOException e) {
                    log.error("Error while clean reader and writer.", e);
                }
            }

        }
    }

    /**
     * Run read system socket
     */
    private void runSocket() {
        while (!this.socket.isClosed()) {
            final String data;

            try {
                data = reader.readLine();

                if (!Strings.isNullOrEmpty(data)) {
                    this.receiveData(data);
                }
            } catch (IOException e) {
                log.error("SocketClient: lecture erreur {}", e.getMessage());
            }

        }
    }

    private void runMessagePool() {
        while (this.socket != null && !this.socket.isClosed()) {

            if (this.socket.isConnected() && this.writer != null) {
                final String message = this.messageQueue.poll();

                if (!Strings.isNullOrEmpty(message)) {
                    this.writer.println(message);
                }
            }

            try {
                Thread.sleep(this.cooldownMessages);
            } catch (InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Nullable
    public final Socket getSocket() {
        return socket;
    }

    public final void setCooldownMessages(final int cooldown) {
        this.cooldownMessages = cooldown;
    }

    public abstract void receiveData(final String data);
}
