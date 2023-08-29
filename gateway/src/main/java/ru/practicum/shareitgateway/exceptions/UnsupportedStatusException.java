package ru.practicum.shareitgateway.exceptions;

public class UnsupportedStatusException extends RuntimeException {

    public UnsupportedStatusException(String message) {
        super(message);
    }
}
