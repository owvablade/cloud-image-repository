package ru.polytech.cloudimagerepository.exception;

public class FileIsEmptyException extends RuntimeException {

    public FileIsEmptyException(String message) {
        super(message);
    }
}
