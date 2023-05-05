package com.funixproductions.core.tools.socket;

import com.funixproductions.core.exceptions.ApiException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class SpringSocketTests {

    private final String ip = "127.0.0.1";
    private final int port = 2525;
    private final AtomicBoolean atomicBoolean = new AtomicBoolean(false);

    @Test
    void testServer() {
        try {
            final String toCheck = "oui je suis un test";
            final TestServerSocket server = new TestServerSocket(port, atomicBoolean, toCheck);
            final TestClientSocket client = new TestClientSocket(ip, port, atomicBoolean, toCheck);

            client.sendMessage(toCheck);

            final Instant start = Instant.now();
            while (!atomicBoolean.get()) {
                if (Instant.now().getEpochSecond() - start.getEpochSecond() > 15) {
                    fail("Timeout");
                }
            }

            assertTrue(atomicBoolean.get());
            server.closeServer();
            client.close();
        } catch (ApiException e) {
            fail(e);
        }
    }

}
