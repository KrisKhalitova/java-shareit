package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleUnsupportedStatusExceptionTest() {
        UnsupportedStatusException e = new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        ErrorResponse errorResponse = errorHandler.handle(e);

        assertNotNull(errorResponse);
        assertEquals(errorResponse.getDescription(), "Unknown state: UNSUPPORTED_STATUS");
    }

    @Test
    void handleValidationExceptionTest() {
        ValidationException e = new ValidationException("Ошибка в запросе");
        ErrorResponse errorResponse = errorHandler.handleValidationException(e);

        assertNotNull(errorResponse);
        assertEquals(errorResponse.getDescription(), "Ошибка в запросе");
    }

    @Test
    void handleNotFoundExceptionTest() {
        NotFoundException e = new NotFoundException("Не найдено.");
        ErrorResponse errorResponse = errorHandler.handleNotFoundException(e);

        assertNotNull(errorResponse);
        assertEquals(errorResponse.getDescription(), "Не найдено.");
    }
}
