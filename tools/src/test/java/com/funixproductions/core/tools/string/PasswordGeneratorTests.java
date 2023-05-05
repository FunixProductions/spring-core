package com.funixproductions.core.tools.string;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PasswordGeneratorTests {

    @Test
    void testPassword2Digit2Specials2Upper2Down() {
        final PasswordGenerator passwordGenerator = new PasswordGenerator();
        passwordGenerator.setSpecialCharsAmount(2);
        passwordGenerator.setNumbersAmount(2);
        passwordGenerator.setAlphaUpper(2);
        passwordGenerator.setAlphaDown(2);

        final String password = passwordGenerator.generateRandomPassword();
        checkPassword(password, 2, 2, 2, 2);
    }

    @Test
    void testPassword4Digit4Specials4Upper4Down() {
        final PasswordGenerator passwordGenerator = new PasswordGenerator();
        passwordGenerator.setSpecialCharsAmount(4);
        passwordGenerator.setNumbersAmount(4);
        passwordGenerator.setAlphaUpper(4);
        passwordGenerator.setAlphaDown(4);

        final String password = passwordGenerator.generateRandomPassword();
        checkPassword(password, 4, 4, 4, 4);
    }

    @Test
    void testPassword4Digit5Specials6Upper7Down() {
        final PasswordGenerator passwordGenerator = new PasswordGenerator();
        passwordGenerator.setSpecialCharsAmount(4);
        passwordGenerator.setNumbersAmount(5);
        passwordGenerator.setAlphaUpper(6);
        passwordGenerator.setAlphaDown(7);

        final String password = passwordGenerator.generateRandomPassword();
        checkPassword(password, 4, 5, 6, 7);
    }

    private void checkPassword(final String password,
                               final int specialProperty,
                               final int numbersProperty,
                               final int upperProperty,
                               final int downProperty) {
        int specials = 0;
        int numbers = 0;
        int upper = 0;
        int down = 0;

        for (char c : password.toCharArray()) {
            if (c >= 33 && c <= 47) {
                ++specials;
            } else if (c >= '0' && c <= '9') {
                ++numbers;
            } else if (c >= 'a' && c <= 'z') {
                ++down;
            } else if (c >= 'A' && c <= 'Z') {
                ++upper;
            }
        }

        assertEquals(specialProperty, specials);
        assertEquals(numbersProperty, numbers);
        assertEquals(upperProperty, upper);
        assertEquals(downProperty, down);
    }

}
