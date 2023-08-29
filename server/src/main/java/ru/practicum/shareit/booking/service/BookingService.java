package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.PostBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.Collection;

public interface BookingService {
    ResponseBookingDto addNewRequestForBooking(PostBookingDto postBookingDto, Long bookerId);

    ResponseBookingDto approveBooking(Long bookingId, boolean approved, Long userId);

    ResponseBookingDto getBookingByBookingId(Long bookingId, Long userId);

    Collection<ResponseBookingDto> getAllBookingsByUserIdByState(BookingState state, Long userId, Integer from, Integer size);

    Collection<ResponseBookingDto> getAllBookingsByOwnerByState(BookingState state, Long ownerId, Integer from, Integer size);
}
