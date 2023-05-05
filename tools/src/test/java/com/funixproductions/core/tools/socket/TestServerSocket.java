package com.funixproductions.core.tools.socket;

import com.funixproductions.core.exceptions.ApiException;
import lombok.Getter;

import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
public class TestServerSocket extends ApiServerSocket {
    private final Set<TestClientSocket> clientSockets = new HashSet<>();
    private final AtomicBoolean passed;
    private final String toCheck;

    public TestServerSocket(int port, final AtomicBoolean atomicBoolean, final String toCheck) throws ApiException {
        super(port);
        this.passed = atomicBoolean;
        this.toCheck = toCheck;
    }

    @Override
    public void newClient(Socket socket) {
        final TestClientSocket client = new TestClientSocket(socket, passed, toCheck);
        client.sendMessage(toCheck);
        clientSockets.add(client);
    }
}
