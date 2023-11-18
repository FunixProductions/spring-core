package com.funixproductions.core.tools.classpath;

import com.funixproductions.core.exceptions.ApiException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ImageReaderClasspathTest {

    @Test
    void testReadImageSuccess() {
        final ImageReaderClasspath imageReaderClasspath = new ImageReaderClasspath(
                this.getClass(),
                "logo-pacifista.png",
                "png"
        );

        assertDoesNotThrow(() -> {
            imageReaderClasspath.getBufferedImage();
            assertTrue(imageReaderClasspath.getBytes().length > 0);
        });
    }

    @Test
    void testReadImageNotValidFormat() {
        final ImageReaderClasspath imageReaderClasspath = new ImageReaderClasspath(
                this.getClass(),
                "logo-pacifista.png",
                "jpg"
        );

        assertThrowsExactly(ApiException.class, () -> {
            imageReaderClasspath.getBufferedImage();
            assertTrue(imageReaderClasspath.getBytes().length > 0);
        });
    }

    @Test
    void testReadImageNotValidPath() {
        assertThrowsExactly(ApiException.class, () -> {
            new ImageReaderClasspath(
                    this.getClass(),
                    "logo-pacifista.jpg",
                    "png"
            );
        });
    }

}
