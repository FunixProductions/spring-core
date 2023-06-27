package com.funixproductions.core.tools.encryption;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Need env configured to run this test
 * You need to set the environment variables ENCRYPTION_KEY and ENCRYPTION_IV
 */
@Slf4j
class TestEncryption {

    @Test
    void testEncryptAndDecryptWithEnv() {
        final Encryption encryption = new Encryption() {};

        final String str = "Bonjour je suis encrypté";
        final String encoded = encryption.convertToDatabase(str);
        final String decoded = encryption.convertToEntity(encoded);

        assertNotEquals(str, encoded);
        assertEquals(str, decoded);
    }

    @Test
    void testFetchNewEncryptionKey() {
        final String key = Encryption.generateNewEncryptionKey();

        log.info("New encryption key: {}", key);
        assertFalse(Strings.isNullOrEmpty(key));
    }

    @Test
    void testFetchNewEncryptionIvKey() {
        final String key = Encryption.generateIvEncryptionKey();

        log.info("New encryption iv key: {}", key);
        assertFalse(Strings.isNullOrEmpty(key));
    }
}
