package com.funixproductions.core.tools.encryption;

import com.google.common.base.Strings;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Need env configured to run this test
 * You need to set the environment variables ENCRYPTION_KEY and ENCRYPTION_IV
 */
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
        assertFalse(Strings.isNullOrEmpty(Encryption.generateNewEncryptionKey()));
    }

    @Test
    void testFetchNewEncryptionIvKey() {
        assertFalse(Strings.isNullOrEmpty(Encryption.generateIvEncryptionKey()));
    }
}
