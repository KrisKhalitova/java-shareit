package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerId(Long bookerId, Sort sort);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime dateTime,
                                                             LocalDateTime dateTime1, Sort sort);

    List<Booking> findAllByBookerIdAndEndBefore(Long bookerId, LocalDateTime dateTime, Sort sort);

    List<Booking> findAllByBookerIdAndStartAfter(Long bookerId, LocalDateTime dateTime, Sort sort);

    List<Booking> findAllByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    Boolean existsByBookerIdAndEndBeforeAndStatus(Long bookerId, LocalDateTime localDateTime, BookingStatus status);

    List<Booking> findAllByItemOwnerId(Long ownerId, Sort sort);

    List<Booking> findAllByItemOwnerIdAndEndBefore(Long bookerId, LocalDateTime dateTime, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime dateTime,
                                                                LocalDateTime anotherDateTime, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStartAfter(Long bookerId, LocalDateTime dateTime, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findBookingByItemIdAndStartBefore(Long itemId, LocalDateTime dateTime, Sort sort);

    List<Booking> findBookingByItemIdAndStartAfterAndStatus(Long itemId, LocalDateTime now, BookingStatus status, Sort sort);
}
