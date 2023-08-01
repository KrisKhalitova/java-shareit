package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.PostBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseBookingDto addNewRequestForBooking(@Valid @RequestBody PostBookingDto postBookingDto,
                                                      @RequestHeader(USER_ID_HEADER) Long bookerId) {
        log.info("Создан запрос на бронирование вещи c id {} от пользователя с id {}", postBookingDto.getId(), bookerId);
        ResponseBookingDto bookingDto = bookingService.addNewRequestForBooking(postBookingDto, bookerId);
        return bookingDto;
    }

    @PatchMapping("/{bookingId}")
    public ResponseBookingDto approveBooking(@PathVariable Long bookingId, @RequestParam boolean approved,
                                             @RequestHeader(USER_ID_HEADER) Long bookerId) {
        log.info("Получен запрос на подтверждение бронирования вещи c id {} от пользователя с id {}", bookingId, bookerId);
        ResponseBookingDto bookingDto = bookingService.approveBooking(bookingId, approved, bookerId);
        return bookingDto;
    }

    @GetMapping("/{bookingId}")
    public ResponseBookingDto getBookingByBookingId(@PathVariable Long bookingId, @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Получен запрос на получение информации по бронированию вещи c id {}", bookingId);
        ResponseBookingDto bookingDto = bookingService.getBookingByBookingId(bookingId, userId);
        return bookingDto;
    }

    @GetMapping
    public Collection<ResponseBookingDto> getAllBookingsByUserIdByState(@RequestParam(value = "state", defaultValue = "ALL") BookingState state,
                                                                        @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Получен запрос на получение информации по всем бронированиям пользователя с id {} ", userId);
        Collection<ResponseBookingDto> allBookingsByUserIdByState = bookingService.getAllBookingsByUserIdByState(state, userId);
        return allBookingsByUserIdByState;
    }

    @GetMapping("/owner")
    public Collection<ResponseBookingDto> getAllBookingsByOwnerByState(@RequestParam(value = "state", defaultValue = "ALL") BookingState state,
                                                                       @RequestHeader(USER_ID_HEADER) Long ownerId) {
        log.info("Получен запрос на получение информации по всем вещам пользователя с id {} ", ownerId);
        Collection<ResponseBookingDto> bookings = bookingService.getAllBookingsByOwnerByState(state, ownerId);
        return bookings;
    }
}