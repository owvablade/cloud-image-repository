package ru.polytech.cloudimagerepository.util;

import ru.polytech.cloudimagerepository.exception.ImageCreationException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtil {

    public static BufferedImage createBufferedImageFromBytes(byte[] bytes) {
        InputStream is = new ByteArrayInputStream(bytes);
        try {
            return ImageIO.read(is);
        } catch (IOException e) {
            throw new ImageCreationException("Error while converting byte array to buffered image");
        }
    }
}
