package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;

public class EmailException extends RuntimeException {

    public EmailException(HttpStatus badRequest, String message) {
        super(message);
    }
}
