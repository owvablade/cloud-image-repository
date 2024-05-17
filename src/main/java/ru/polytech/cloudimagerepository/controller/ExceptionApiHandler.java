package ru.polytech.cloudimagerepository.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.polytech.cloudimagerepository.exception.*;
import ru.polytech.cloudimagerepository.model.ErrorMessage;

@RestControllerAdvice
public class ExceptionApiHandler {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ExceptionApiHandler.class);

    @ExceptionHandler({ImageNotFoundException.class,
            ImageMetadataNotFoundException.class,
            ImageAlreadyExistsException.class})
    public ResponseEntity<ErrorMessage> handleImageException(final RuntimeException e) {
        log.info("Image exception: ", e);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage(e.getMessage()));
    }

    @ExceptionHandler({FileIsEmptyException.class,
            FileContentTypeNotFoundException.class,
            FileIsNotImageException.class})
    public ResponseEntity<ErrorMessage> handleInputFileException(final RuntimeException e) {
        log.info("Input file exception: ", e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(e.getMessage()));
    }

    @ExceptionHandler(IncorrectIdFormatException.class)
    public ResponseEntity<ErrorMessage> handleIncorrectIdFormatException(final IncorrectIdFormatException e) {
        log.info("Input file exception: ", e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(e.getMessage()));
    }

    @ExceptionHandler({ClusterUpdateException.class})
    public ResponseEntity<ErrorMessage> handleClusterException(final ClusterUpdateException e) {
        log.info("Cluster exception: ", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorMessage(e.getMessage()));
    }

    @ExceptionHandler(ImageCreationException.class)
    public ResponseEntity<ErrorMessage> handleImageCreationException(final ImageCreationException e) {
        log.info("Image creation exception: ", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorMessage(e.getMessage()));
    }
}
