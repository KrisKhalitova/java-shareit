package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;

public class NotFoundException extends RuntimeException {

    public NotFoundException(HttpStatus notFound, String message) {
        super(message);
    }
}
