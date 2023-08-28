package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.PostBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.ErrorHandler;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnsupportedStatusException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utils.ShareItPageRequest;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    public static final Sort SORT_BY_DESC = Sort.by(Sort.Direction.DESC, "start");

    @Override
    public ResponseBookingDto addNewRequestForBooking(PostBookingDto bookingDto, Long bookerId) {
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() ->
                new NotFoundException("Вещь не найдена"));
        User user = userRepository.findById(bookerId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден"));
        if (item.getOwner().getId().equals(bookerId)) {
            throw new NotFoundException("Id владельца и пользователя не могут совпадать");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь не доступна к бронированию");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new ValidationException("Неверно введены параметры времени бронирования");
        }
        if (bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new ValidationException("Неверно введены параметры времени бронирования");
        }
        long count = bookingRepository.countByItemIdAndStatusAndStartBeforeAndEndAfter(
                bookingDto.getItemId(), BookingStatus.APPROVED, bookingDto.getEnd(), bookingDto.getStart());
        if (count > 0) {
            throw new ValidationException("Вещь уже забронирована в данный промежуток времени");
        }
        Booking booking = BookingMapper.toBooking(bookingDto, item, user);
        return BookingMapper.toResponseBookingDto(bookingRepository.save(booking));
    }

    @Override
    public ResponseBookingDto approveBooking(Long bookingId, boolean approved, Long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден"));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Бронирование не найдено"));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Не совпадают id по бронированию");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Статус бронирования не 'В ожидании'");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toResponseBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseBookingDto getBookingByBookingId(Long bookingId, Long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден"));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Бронирование не найдено"));
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Несовпадение по бронированию");
        }
        return BookingMapper.toResponseBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ResponseBookingDto> getAllBookingsByUserIdByState(BookingState state, Long userId, Integer from, Integer size) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден"));
        List<Booking> bookingList;
        LocalDateTime localDateTime = LocalDateTime.now();
        Pageable pageable = new ShareItPageRequest(from, size, SORT_BY_DESC);
        switch (state) {
            case ALL:
                bookingList = bookingRepository.findAllByBookerId(userId, pageable);
                break;
            case CURRENT:
                bookingList = bookingRepository.findAllByBookerIdAndDateTimeBetween(userId, localDateTime, pageable);
                break;
            case PAST:
                bookingList = bookingRepository.findAllByBookerIdAndEndBefore(userId, localDateTime, pageable);
                break;
            case FUTURE:
                bookingList = bookingRepository.findAllByBookerIdAndStartAfter(userId, localDateTime, pageable);
                break;
            case WAITING:
                bookingList = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookingList = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED, pageable);
                break;
            default:
                throw new UnsupportedStatusException(ErrorHandler.UNSUPPORTED_STATUS);
        }
        return bookingList.stream()
                .map(BookingMapper::toResponseBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ResponseBookingDto> getAllBookingsByOwnerByState(BookingState state, Long ownerId, Integer from, Integer size) {
        userRepository.findById(ownerId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден"));
        List<Booking> bookingList;
        LocalDateTime localDateTime = LocalDateTime.now();
        Pageable pageable = new ShareItPageRequest(from, size, SORT_BY_DESC);
        switch (state) {
            case ALL:
                bookingList = bookingRepository.findAllByItemOwnerId(ownerId, pageable);
                break;
            case PAST:
                bookingList = bookingRepository.findAllByItemOwnerIdAndEndBefore(ownerId, localDateTime, pageable);
                break;
            case CURRENT:
                bookingList = bookingRepository.findAllByOwnerIdAndDateTimeBetween(ownerId, localDateTime, pageable);
                break;
            case FUTURE:
                bookingList = bookingRepository.findAllByItemOwnerIdAndStartAfter(ownerId, localDateTime, pageable);
                break;
            case WAITING:
                bookingList = bookingRepository.findAllByItemOwnerIdAndStatus(ownerId, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookingList = bookingRepository.findAllByItemOwnerIdAndStatus(ownerId, BookingStatus.REJECTED, pageable);
                break;
            default:
                throw new UnsupportedStatusException(ErrorHandler.UNSUPPORTED_STATUS);
        }
        return bookingList.stream()
                .map(BookingMapper::toResponseBookingDto)
                .collect(Collectors.toList());
    }
}
