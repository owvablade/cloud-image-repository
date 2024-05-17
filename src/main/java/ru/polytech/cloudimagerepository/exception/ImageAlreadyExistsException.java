package ru.polytech.cloudimagerepository.exception;

public class ImageAlreadyExistsException extends RuntimeException {

    public ImageAlreadyExistsException(String message) {
        super(message);
    }
}
