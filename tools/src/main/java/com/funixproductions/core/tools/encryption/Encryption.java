package com.funixproductions.core.tools.encryption;

import com.funixproductions.core.exceptions.ApiException;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.*;
import java.util.Base64;

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
        this.key = getKeyFromFile();
        this.iv = getIvFromFile();
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

    private static Key getKeyFromFile() {
        final Base64.Encoder base64Encoder = Base64.getEncoder();
        final Base64.Decoder base64Decoder = Base64.getDecoder();
        final File keyFile = new File("crypt.key");

        try {
            if (!keyFile.exists()) {
                if (!keyFile.createNewFile()) {
                    throw new ApiException("Impossible de créer un nouveau fichier d'encryption.");
                }

                final KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM_KEY);
                keyGenerator.init(KEY_SIZE);
                final Key key = keyGenerator.generateKey();

                final String keyString = base64Encoder.encodeToString(key.getEncoded());
                writeInFile(keyString, keyFile);
                return key;
            }

            final String keyString = Files.readString(keyFile.toPath(), StandardCharsets.UTF_8);
            final byte[] decodedKey = base64Decoder.decode(keyString);
            return new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM_KEY);
        } catch (IOException | NoSuchAlgorithmException e)  {
            throw new ApiException("Une erreur est survenue lors de la création du fichier d'encryption.", e);
        }
    }

    private static byte[] getIvFromFile() {
        final Base64.Encoder base64Encoder = Base64.getEncoder();
        final Base64.Decoder base64Decoder = Base64.getDecoder();
        final File ivFile = new File("crypt.iv");

        try {
            if (!ivFile.exists()) {
                if (!ivFile.createNewFile()) {
                    throw new ApiException("Impossible de créer un nouveau fichier d'encryption.");
                }

                final SecureRandom secureRandom = new SecureRandom();
                final byte[] iv = new byte[IV_LENGTH_BYTE];
                secureRandom.nextBytes(iv);

                final String ivString = base64Encoder.encodeToString(iv);
                writeInFile(ivString, ivFile);
                return iv;
            }

            final String ivString = Files.readString(ivFile.toPath(), StandardCharsets.UTF_8);
            return base64Decoder.decode(ivString);
        } catch (IOException e)  {
            throw new ApiException("Une erreur est survenue lors de la création du fichier d'encryption.", e);
        }
    }

    private static void writeInFile(final String data, final File file) throws IOException {
        try {
            Files.writeString(file.toPath(), data, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            Files.delete(file.toPath());
            throw e;
        }
    }

    public Key getKey() {
        return key;
    }
}
