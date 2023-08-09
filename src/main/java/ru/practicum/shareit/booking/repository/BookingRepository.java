package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerId(Long bookerId, Pageable pageable);

    @Query("select b from Booking b where b.booker.id = ?1 and b.start < ?2 and b.end > ?2")
    List<Booking> findAllByBookerIdAndDateTimeBetween(Long bookerId, LocalDateTime dateTime, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndBefore(Long bookerId, LocalDateTime dateTime, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartAfter(Long bookerId, LocalDateTime dateTime, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatus(Long bookerId, BookingStatus status, Pageable pageable);

    Boolean existsByItemIdAndBookerIdAndEndBeforeAndStatus(Long itemId, Long bookerId, LocalDateTime localDateTime, BookingStatus status);

    List<Booking> findAllByItemOwnerId(Long ownerId, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndEndBefore(Long bookerId, LocalDateTime dateTime, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start < ?2 and b.end > ?2")
    List<Booking> findAllByOwnerIdAndDateTimeBetween(Long bookerId, LocalDateTime localDateTime, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartAfter(Long bookerId, LocalDateTime dateTime, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStatus(Long bookerId, BookingStatus status, Pageable pageable);

    List<Booking> findBookingByItemIdAndStartBefore(Long itemId, LocalDateTime dateTime, Sort sort);

    List<Booking> findBookingByItemIdAndStartAfterAndStatus(Long itemId, LocalDateTime now, BookingStatus status, Sort sort);
}
