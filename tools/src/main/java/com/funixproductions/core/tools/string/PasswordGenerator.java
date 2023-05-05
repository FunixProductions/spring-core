package com.funixproductions.core.tools.string;

import com.funixproductions.core.exceptions.ApiException;
import lombok.Setter;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Setter
public class PasswordGenerator {

    private int specialCharsAmount = 2;
    private int numbersAmount = 2;
    private int alphaUpper = 10;
    private int alphaDown = 12;

    public String generateRandomPassword() throws ApiException {
        try {
            final Stream<Character> passwordStream = Stream.concat(
                    generateRandomSpecialChars(this.specialCharsAmount),
                    Stream.concat(generateRandomNumbers(this.numbersAmount),
                            Stream.concat(generateRandomAlphabets(this.alphaUpper, true),
                                    generateRandomAlphabets(this.alphaDown, false)
                            )));
            final List<Character> charList = passwordStream.collect(Collectors.toList());
            Collections.shuffle(charList);

            final StringBuilder stringBuilder = new StringBuilder();
            for (char c : charList) {
                stringBuilder.append(c);
            }
            return stringBuilder.toString();
        } catch (IllegalArgumentException e) {
            throw new ApiException("Une erreur est survenue lors de la génération du mot de passe.", e);
        }
    }

    private Stream<Character> generateRandomSpecialChars(int count) {
        final Random random = new SecureRandom();
        final IntStream specialChars = random.ints(count, 35, 38);
        return specialChars.mapToObj(data -> (char) data);
    }

    private Stream<Character> generateRandomAlphabets(int count, boolean uppercase) {
        final Random random = new SecureRandom();
        final IntStream chars;

        if (uppercase) {
            chars = random.ints(count, 65, 90);
        } else {
            chars = random.ints(count, 97, 122);
        }

        return chars.mapToObj(data -> (char) data);
    }

    private Stream<Character> generateRandomNumbers(int count) {
        final Random random = new SecureRandom();
        final IntStream numbers = random.ints(count, 48, 57);
        return numbers.mapToObj(data -> (char) data);
    }

}
