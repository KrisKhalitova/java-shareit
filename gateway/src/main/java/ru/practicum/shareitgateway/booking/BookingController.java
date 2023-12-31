package ru.practicum.shareitgateway.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitgateway.booking.dto.BookingState;
import ru.practicum.shareitgateway.booking.dto.PostBookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    public static final String DEFAULT_FROM_VALUE = "0";
    public static final String DEFAULT_SIZE_VALUE = "20";

    @PostMapping
    public ResponseEntity<Object> addNewRequestForBooking(@Valid @RequestBody PostBookingDto postBookingDto,
                                                          @RequestHeader(USER_ID_HEADER) Long bookerId) {
        log.info("Создан запрос на бронирование вещи c id {} от пользователя с id {}", postBookingDto.getId(), bookerId);
        ResponseEntity<Object> bookingDto = bookingClient.addNewRequestForBooking(bookerId, postBookingDto);
        return bookingDto;
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@PathVariable Long bookingId, @RequestParam boolean approved,
                                                 @RequestHeader(USER_ID_HEADER) Long bookerId) {
        log.info("Получен запрос на подтверждение бронирования вещи c id {} от пользователя с id {}", bookingId, bookerId);
        ResponseEntity<Object> bookingDto = bookingClient.approveBooking(bookerId, bookingId, approved);
        return bookingDto;
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingByBookingId(@PathVariable Long bookingId, @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Получен запрос на получение информации по бронированию вещи c id {}", bookingId);
        ResponseEntity<Object> bookingDto = bookingClient.getBookingByBookingId(bookingId, userId);
        return bookingDto;
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsByUserIdByState(@RequestParam(value = "state", defaultValue = "ALL") BookingState state,
                                                                @RequestHeader(USER_ID_HEADER) Long userId,
                                                                @RequestParam(defaultValue = DEFAULT_FROM_VALUE)
                                                                @PositiveOrZero Integer from,
                                                                @RequestParam(defaultValue = DEFAULT_SIZE_VALUE)
                                                                @Positive Integer size) {
        log.info("Получен запрос на получение информации по всем бронированиям пользователя с id {} ", userId);
        ResponseEntity<Object> allBookingsByUserIdByState = bookingClient.getAllBookingsByUserIdByState(userId, state, from, size);
        return allBookingsByUserIdByState;
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsByOwnerByState(@RequestParam(value = "state", defaultValue = "ALL") BookingState state, @RequestHeader(USER_ID_HEADER) Long ownerId,
                                                               @RequestParam(defaultValue = DEFAULT_FROM_VALUE)
                                                               @PositiveOrZero Integer from,
                                                               @RequestParam(defaultValue = DEFAULT_SIZE_VALUE)
                                                               @Positive Integer size) {
        log.info("Получен запрос на получение информации по всем вещам пользователя с id {} ", ownerId);
        ResponseEntity<Object> bookings = bookingClient.getAllBookingsByOwnerByState(ownerId, state, from, size);
        return bookings;
    }
}
