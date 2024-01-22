package com.funixproductions.core.tools.string;

import com.funixproductions.core.exceptions.ApiException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtils {

    public static String readFromClasspath(@NonNull final String path, @NonNull final Class<?> clazz) throws ApiException {
        try (final InputStream inputStream = clazz.getClassLoader().getResourceAsStream(path)) {
            if (inputStream != null) {
                try (final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                     final BufferedReader reader = new BufferedReader(inputStreamReader)) {
                    final StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    return stringBuilder.toString();
                }
            } else {
                throw new IOException("Read, input stream null. Path: " + path + " - Class: " + clazz.getName() + " .");
            }
        } catch (Exception e) {
            final String errorMessage = "Une erreur interne lors de la lecture du fichier " + path + " est survenue.";

            log.error(errorMessage, e);
            throw new ApiException(errorMessage, e);
        }
    }

}
