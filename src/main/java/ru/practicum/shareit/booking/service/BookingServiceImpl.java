package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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
import ru.practicum.shareit.exceptions.UnsupportedStatusException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

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
                new ValidationException(HttpStatus.NOT_FOUND, "Вещь не найдена"));
        User user = userRepository.findById(bookerId).orElseThrow(() ->
                new ValidationException(HttpStatus.NOT_FOUND, "Пользователь не найден"));
        if (item.getOwner().getId().equals(bookerId)) {
            throw new ValidationException(HttpStatus.NOT_FOUND, "Id владельца и пользователя не могут совпадать");
        }
        if (!item.getAvailable()) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Вещь не доступна к бронированию");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) || bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Неверно введены параметры времени бронирования");
        }
        Booking booking = BookingMapper.toBooking(bookingDto, item, user);
        return BookingMapper.toResponseBookingDto(bookingRepository.save(booking));
    }

    @Override
    public ResponseBookingDto approveBooking(Long bookingId, boolean approved, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new ValidationException(HttpStatus.NOT_FOUND, "Бронирование не найдено"));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ValidationException(HttpStatus.NOT_FOUND, "Не совпадают id по бронированию");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Статус бронирования не 'В ожидании'");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toResponseBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseBookingDto getBookingByBookingId(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new ValidationException(HttpStatus.NOT_FOUND, "Бронирование не найдено"));
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new ValidationException(HttpStatus.NOT_FOUND, "Несовпадение по бронированию");
        }
        return BookingMapper.toResponseBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ResponseBookingDto> getAllBookingsByUserIdByState(BookingState state, Long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new ValidationException(HttpStatus.NOT_FOUND, "Пользователь не найден"));
        List<Booking> bookingList;
        LocalDateTime localDateTime = LocalDateTime.now();
        switch (state) {
            case ALL:
                bookingList = bookingRepository.findAllByBookerId(userId, SORT_BY_DESC);
                break;
            case CURRENT:
                bookingList = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(userId, localDateTime, localDateTime, SORT_BY_DESC);
                break;
            case PAST:
                bookingList = bookingRepository.findAllByBookerIdAndEndBefore(userId, localDateTime, SORT_BY_DESC);
                break;
            case FUTURE:
                bookingList = bookingRepository.findAllByBookerIdAndStartAfter(userId, localDateTime, SORT_BY_DESC);
                break;
            case WAITING:
                bookingList = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING, SORT_BY_DESC);
                break;
            case REJECTED:
                bookingList = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED, SORT_BY_DESC);
                break;
            case UNSUPPORTED_STATUS:
                throw new UnsupportedStatusException(HttpStatus.BAD_REQUEST, ErrorHandler.UNSUPPORTED_STATUS);
            default:
                throw new UnsupportedStatusException(HttpStatus.BAD_REQUEST, ErrorHandler.UNSUPPORTED_STATUS);
        }
        return bookingList.stream()
                .map(BookingMapper::toResponseBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ResponseBookingDto> getAllBookingsByOwnerByState(BookingState state, Long ownerId) {
        userRepository.findById(ownerId).orElseThrow(() ->
                new ValidationException(HttpStatus.NOT_FOUND, "Пользователь не найден"));
        List<Booking> bookingList;
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                bookingList = bookingRepository.findAllByItemOwnerId(ownerId, SORT_BY_DESC);
                break;
            case PAST:
                bookingList = bookingRepository.findAllByItemOwnerIdAndEndBefore(ownerId, now, SORT_BY_DESC);
                break;
            case CURRENT:
                bookingList = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(ownerId, now, now, SORT_BY_DESC);
                break;
            case FUTURE:
                bookingList = bookingRepository.findAllByItemOwnerIdAndStartAfter(ownerId, now, SORT_BY_DESC);
                break;
            case WAITING:
                bookingList = bookingRepository.findAllByItemOwnerIdAndStatus(ownerId, BookingStatus.WAITING, SORT_BY_DESC);
                break;
            case REJECTED:
                bookingList = bookingRepository.findAllByItemOwnerIdAndStatus(ownerId, BookingStatus.REJECTED, SORT_BY_DESC);
                break;
            case UNSUPPORTED_STATUS:
                throw new UnsupportedStatusException(HttpStatus.BAD_REQUEST, ErrorHandler.UNSUPPORTED_STATUS);
            default:
                throw new UnsupportedStatusException(HttpStatus.BAD_REQUEST, ErrorHandler.UNSUPPORTED_STATUS);
        }
        return bookingList.stream()
                .map(BookingMapper::toResponseBookingDto)
                .collect(Collectors.toList());
    }
}
