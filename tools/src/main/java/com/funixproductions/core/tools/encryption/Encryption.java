package com.funixproductions.core.tools.encryption;

import com.funixproductions.core.exceptions.ApiException;
import com.google.common.base.Strings;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

/**
 * Class used to encrypt and decrypt data
 * You need to set the environment variables ENCRYPTION_KEY and ENCRYPTION_IV
 */
public abstract class Encryption {
    private static final String ALGORITHM_KEY = "AES";
    private static final String CRYPT_ALGORITHM = "AES/GCM/NoPadding";
    private static final int KEY_SIZE = 256;
    private static final int IV_LENGTH_BYTE = 16;
    private static final int TAG_LENGTH_BIT = 128;

    private final Base64.Encoder base64Encoder;
    private final Base64.Decoder base64Decoder;
    private final Key key;
    private final byte[] iv;

    protected Encryption() {
        this.key = getKeyFromEnv();
        this.iv = getIvFromEnv();
        this.base64Encoder = Base64.getEncoder();
        this.base64Decoder = Base64.getDecoder();
    }

    public synchronized String convertToDatabase(final String object) throws ApiException {
        try {
            if (object == null) {
                return null;
            }

            final Cipher cipher = Cipher.getInstance(CRYPT_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BIT, iv));

            final byte[] objectBytes = object.getBytes(StandardCharsets.UTF_8);
            final byte[] encrypted = cipher.doFinal(objectBytes);
            return base64Encoder.encodeToString(encrypted);
        } catch (IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new ApiException("Une erreur est survenue lors de l'encryption.", e);
        }
    }

    public synchronized String convertToEntity(final String dbData) throws ApiException {
        try {
            if (dbData == null) {
                return null;
            }

            final Cipher cipher = Cipher.getInstance(CRYPT_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BIT, iv));

            final byte[] base64Decoded = base64Decoder.decode(dbData);
            final byte[] decrypted = cipher.doFinal(base64Decoded);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new ApiException("Une erreur est survenue lors du décryptage.", e);
        }
    }

    private static Key getKeyFromEnv() {
        final String keyStringEnv = System.getenv("ENCRYPTION_KEY");

        if (Strings.isNullOrEmpty(keyStringEnv)) {
            throw new ApiException("Impossible de récupérer la clé d'encryption de l'environement ENCRYPTION_KEY.");
        } else {
            final byte[] decodedKey = Base64.getDecoder().decode(keyStringEnv);
            return new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM_KEY);
        }
    }

    private static byte[] getIvFromEnv() {
        final String ivStringEnv = System.getenv("ENCRYPTION_IV");

        if (Strings.isNullOrEmpty(ivStringEnv)) {
            throw new ApiException("Impossible de récupérer le vecteur d'initialisation de l'environement ENCRYPTION_IV.");
        } else {
            return Base64.getDecoder().decode(ivStringEnv);
        }
    }

    public static String generateNewEncryptionKey() {
        try {
            final KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM_KEY);
            keyGenerator.init(KEY_SIZE);
            final Key key = keyGenerator.generateKey();

            return Base64.getEncoder().encodeToString(key.getEncoded());
        } catch (NoSuchAlgorithmException e)  {
            throw new ApiException("Une erreur est survenue lors de la création d'une clé d'encryption.", e);
        }
    }

    public static String generateIvEncryptionKey() {
        final SecureRandom secureRandom = new SecureRandom();
        final byte[] iv = new byte[IV_LENGTH_BYTE];
        secureRandom.nextBytes(iv);

        return Base64.getEncoder().encodeToString(iv);
    }
}
