package ru.polytech.cloudimagerepository.exception;

public class ImageMetadataNotFoundException extends RuntimeException {

    public ImageMetadataNotFoundException(String message) {
        super(message);
    }
}
