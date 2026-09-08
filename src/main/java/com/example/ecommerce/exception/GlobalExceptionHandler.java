package com.example.ecommerce.exception;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("Resource not found exception: {}", ex.getMessage());

        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                "Not Found",
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDetails);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorDetails> handleDuplicateResourceException(DuplicateResourceException ex) {
        log.warn("Duplicate resource exception: {}", ex.getMessage());

        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                "This Email is Exist",
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                null);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDetails);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> handleValidationExceptions(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(
                error -> {
                    errors.put(error.getField(), error.getDefaultMessage());
                });

        log.warn("Validation failed for request. Field errors: {}", errors);

        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                "Validation Failed",
                HttpStatus.BAD_REQUEST.value(),
                "Invalid input data",
                errors);

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDetails> handleIllegalArgumentException(IllegalArgumentException ex) {

        log.warn("Illegal argument exception: {}", ex.getMessage());
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                "Invalid Request Parameter",
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                null);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ErrorDetails> handlePropertyReferenceException(PropertyReferenceException ex) {

        log.warn("Invalid sort property requested: {}", ex.getPropertyName());
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                "Invalid Sort Property",
                HttpStatus.BAD_REQUEST.value(),
                String.format("No property '%s' found for sorting", ex.getPropertyName()),
                null);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception ex) {
        log.error("Unhandled exception occurred: {}", ex.getMessage());

        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                "Internal Server Error",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                null);

        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
