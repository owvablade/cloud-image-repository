package ru.polytech.cloudimagerepository.exception;

public class FileIsNotImageException extends RuntimeException {

    public FileIsNotImageException(String message) {
        super(message);
    }
}
