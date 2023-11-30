package com.funixproductions.core.tools.classpath;

import com.funixproductions.core.exceptions.ApiException;
import lombok.NonNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URL;

/**
 * Class to read image from classpath.
 */
public class ImageReaderClasspath {

    @NonNull
    private final URL imageUrl;

    @NonNull
    private final String format;

    /**
     * Constructor.
     * @param clazz the class origin
     * @param path the path of the image
     * @param format the format of the image (png, jpg, ...)
     */
    public ImageReaderClasspath(@NonNull final Class<?> clazz,
                                @NonNull final String path,
                                @NonNull final String format) throws ApiException {
        this.format = format;

        try {
            final ClassLoader classLoader = clazz.getClassLoader();
            final URL url = classLoader.getResource(path);

            if (url == null) {
                throw new ApiException("L'image n'existe pas dans la classpath: " + path);
            }
            this.imageUrl = url;
        } catch (Exception e) {
            throw new ApiException("Une erreur est survenue lors de la lecture d'une image depuis la classpath.", e);
        }
    }

    @NonNull
    public BufferedImage getBufferedImage() throws ApiException {
        try {
            final BufferedImage bufferedImage = ImageIO.read(imageUrl);

            if (bufferedImage == null) {
                throw new ApiException("L'image n'existe pas dans la classpath: " + imageUrl + " (return null)");
            }
            return bufferedImage;
        } catch (Exception e) {
            throw new ApiException("Une erreur est survenue lors de la lecture d'une image depuis la classpath.", e);
        }
    }

    public byte[] getBytes() throws ApiException {
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            final BufferedImage image = getBufferedImage();

            if (!ImageIO.write(image, this.format, baos)) {
                throw new ApiException("Impossible de récupérer la byte array de l'image: " + imageUrl + " (no appropriate writer is found)");
            }
            return baos.toByteArray();
        } catch (Exception e) {
            throw new ApiException("Une erreur est survenue lors de la lecture d'une image depuis la classpath getBytes.", e);
        }
    }

}
