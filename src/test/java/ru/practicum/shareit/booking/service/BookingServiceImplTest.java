package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.PostBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.service.BookingServiceImpl.SORT_BY_DESC;
import static ru.practicum.shareit.exceptions.ErrorHandler.UNSUPPORTED_STATUS;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    private Booking booking;
    private PostBookingDto postBookingDto;
    private User user;
    private User user2;
    private Item item;
    private final LocalDateTime start = LocalDateTime.now().plusDays(3);
    private final LocalDateTime end = start.plusDays(1);

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "username", "user@mail.ru");
        user2 = new User(2L, "username2", "user2@mail.ru");
        item = new Item(1L, "item", "description to Item", true, user, null, null);
        booking = new Booking(1L, start, end, item, user2, BookingStatus.WAITING);
        postBookingDto = new PostBookingDto(booking.getId(), item.getId(), booking.getStart(), booking.getEnd());
    }

    @Test
    void addNewRequestForBookingTest() {
        when(itemRepository.findById(postBookingDto.getItemId())).thenReturn(Optional.of(item));
        when(userRepository.findById(booking.getBooker().getId())).thenReturn(Optional.of(user2));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        ResponseBookingDto response = bookingService.addNewRequestForBooking(postBookingDto, booking.getBooker().getId());

        assertNotNull(response);
        assertEquals(booking.getId(), response.getId());
        verify(itemRepository).findById(postBookingDto.getItemId());
        verify(userRepository).findById(booking.getBooker().getId());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void addNewRequestForBookingWithWrongItemIdTest() {
        Long invalidItemId = 999L;
        Long validBookerId = 2L;
        postBookingDto.setItemId(invalidItemId);
        postBookingDto.setStart(LocalDateTime.now().plusHours(1));
        postBookingDto.setEnd(LocalDateTime.now().plusHours(2));

        assertThrows(NotFoundException.class, () -> {
            bookingService.addNewRequestForBooking(postBookingDto, validBookerId);
        });
    }

    @Test
    void addNewRequestForBookingWithWrongBookerIdTest() {
        Long invalidBookerId = 999L;

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            bookingService.addNewRequestForBooking(postBookingDto, invalidBookerId);
        });
    }

    @Test
    void addNewRequestForBookingWithSameOwnerAndBookerIdTest() {
        Long ownerAndBookerId = 1L;

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));

        assertThrows(NotFoundException.class, () ->
                bookingService.addNewRequestForBooking(postBookingDto, ownerAndBookerId));
    }

    @Test
    void testAddNewRequestForBookingWithItemNotAvailable() {
        itemRepository.save(item);
        Long itemId = item.getId();
        Long bookerId = user2.getId();
        item.setAvailable(Boolean.FALSE);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(any())).thenReturn(Optional.of(user2));

        assertThrows(ValidationException.class, () -> {
            bookingService.addNewRequestForBooking(postBookingDto, bookerId);
        });
    }

    @Test
    void addNewRequestForBookingWithInvalidDateTest() {
        Long validBookerId = user2.getId();
        itemRepository.save(item);
        LocalDateTime startNew = LocalDateTime.now().plusDays(5);
        LocalDateTime endNew = LocalDateTime.now().minusDays(1);
        postBookingDto.setStart(startNew);
        postBookingDto.setEnd(endNew);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));

        assertThrows(ValidationException.class, () ->
                bookingService.addNewRequestForBooking(postBookingDto, validBookerId));
    }

    @Test
    void approveBookingTest() {
        Long bookingId = booking.getId();
        boolean approved = true;
        Long userId = user.getId();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        ResponseBookingDto response = bookingService.approveBooking(bookingId, approved, userId);

        assertNotNull(response);
        verify(userRepository).findById(userId);
        verify(bookingRepository).findById(bookingId);
        verify(bookingRepository).save(booking);
        verify(bookingRepository).save(any(Booking.class));
        assertEquals(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED, booking.getStatus());
    }

    @Test
    void approveBookingWithWrongUserIdTest() {
        Long invalidUserId = 5L;
        Long bookingId = booking.getId();

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                bookingService.approveBooking(bookingId, true, invalidUserId));
    }

    @Test
    void approveBookingWithWrongBookingIdTest() {
        Long invalidBookingId = 5L;
        Long userId = user2.getId();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                bookingService.approveBooking(invalidBookingId, true, userId));
    }

    @Test
    void approveBookingWithWrongOwnerAndBookerIdTest() {
        Long ownerAndBookerId = 2L;
        Long bookingId = booking.getId();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () ->
                bookingService.approveBooking(bookingId, true, ownerAndBookerId));
    }

    @Test
    void approveBookingWithWrongStatusTest() {
        Long bookingId = booking.getId();
        Long userId = user.getId();
        booking.setStatus(BookingStatus.REJECTED);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () ->
                bookingService.approveBooking(bookingId, true, userId));
    }

    @Test
    void getBookingByBookingId() {
        Long userId = user2.getId();
        Long bookingId = booking.getId();
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        ResponseBookingDto result = bookingService.getBookingByBookingId(bookingId, userId);

        assertEquals(1L, result.getId());
        assertEquals(BookingStatus.WAITING, result.getStatus());
        assertEquals(start, result.getStart());
        assertEquals(end, result.getEnd());
    }

    @Test
    void getBookingByWrongBookingIdTest() {
        Long bookingId = 999L;
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getBookingByBookingId(bookingId, 2L));
    }

    @Test
    void getBookingByBookingIdWithWrongBookerIdTest() {
        Long userId = 999L;

        assertThrows(NotFoundException.class, () -> bookingService.getBookingByBookingId(1L, userId));
    }

    @Test
    public void getBookingByBookingIdWithOwnerAndBookerIdTest() {
        Long userId = 2L;
        booking.setBooker(user);
        item.setOwner(user);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.getBookingByBookingId(1L, userId));
    }

    @Test
    void getAllBookerBookingsByStateTest() {
        Long userId = user2.getId();
        BookingState state = BookingState.ALL;
        Pageable pageable = new ShareItPageRequest(0, 20, SORT_BY_DESC);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(bookingRepository.findAllByBookerId(userId, pageable)).thenReturn(List.of(booking));
        Collection<ResponseBookingDto> resultAll = bookingService.getAllBookingsByUserIdByState(state, userId, 0, 20);

        assertNotNull(resultAll);
        assertEquals(1, resultAll.size());

        state = BookingState.CURRENT;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(bookingRepository.findAllByBookerIdAndDateTimeBetween(anyLong(), any(LocalDateTime.class), any())).thenReturn(List.of(booking));
        Collection<ResponseBookingDto> resultCurrent = bookingService.getAllBookingsByUserIdByState(state, userId, 0, 20);

        assertNotNull(resultCurrent);
        assertEquals(1, resultCurrent.size());

        state = BookingState.PAST;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(bookingRepository.findAllByBookerIdAndEndBefore(anyLong(), any(LocalDateTime.class), any())).thenReturn(List.of(booking));
        Collection<ResponseBookingDto> resultPast = bookingService.getAllBookingsByUserIdByState(state, userId, 0, 20);

        assertNotNull(resultPast);
        assertEquals(1, resultPast.size());

        state = BookingState.FUTURE;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(bookingRepository.findAllByBookerIdAndStartAfter(anyLong(), any(LocalDateTime.class), any())).thenReturn(List.of(booking));
        Collection<ResponseBookingDto> resultFuture = bookingService.getAllBookingsByUserIdByState(state, userId, 0, 20);

        assertNotNull(resultFuture);
        assertEquals(1, resultFuture.size());

        state = BookingState.WAITING;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(bookingRepository.findAllByBookerIdAndStatus(anyLong(), any(), any())).thenReturn(List.of(booking));
        Collection<ResponseBookingDto> resultWaiting = bookingService.getAllBookingsByUserIdByState(state, userId, 0, 20);

        assertNotNull(resultWaiting);
        assertEquals(1, resultWaiting.size());

        state = BookingState.REJECTED;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(bookingRepository.findAllByBookerIdAndStatus(anyLong(), any(), any())).thenReturn(List.of(booking));
        Collection<ResponseBookingDto> resultRejected = bookingService.getAllBookingsByUserIdByState(state, userId, 0, 20);

        assertNotNull(resultRejected);
        assertEquals(1, resultRejected.size());
    }

    @Test
    void getAllBookerBookingsByStateWithUnsupportedStatusTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        UnsupportedStatusException exception = assertThrows(UnsupportedStatusException.class,
                () -> bookingService.getAllBookingsByUserIdByState(BookingState.UNSUPPORTED_STATUS, user2.getId(), 0, 20));

        assertEquals(UNSUPPORTED_STATUS, exception.getMessage());
    }

    @Test
    void getAllBookerBookingsByStateWithWrongUserIdTest() {
        Long userId = 999L;
        BookingState state = BookingState.ALL;

        assertThrows(NotFoundException.class, () -> bookingService.getAllBookingsByUserIdByState(state, userId, 0, 20));
    }

    @Test
    void getAllBookingsByOwnerByStateTest() {
        Long userId = user2.getId();
        BookingState state = BookingState.ALL;
        Pageable pageable = new ShareItPageRequest(0, 20, SORT_BY_DESC);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(bookingRepository.findAllByItemOwnerId(userId, pageable)).thenReturn(List.of(booking));
        Collection<ResponseBookingDto> resultAll = bookingService.getAllBookingsByOwnerByState(state, userId, 0, 20);

        assertNotNull(resultAll);
        assertEquals(1, resultAll.size());

        state = BookingState.CURRENT;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(bookingRepository.findAllByOwnerIdAndDateTimeBetween(anyLong(), any(LocalDateTime.class), any())).thenReturn(List.of(booking));
        Collection<ResponseBookingDto> resultCurrent = bookingService.getAllBookingsByOwnerByState(state, userId, 0, 20);

        assertNotNull(resultCurrent);
        assertEquals(1, resultCurrent.size());

        state = BookingState.PAST;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(bookingRepository.findAllByItemOwnerIdAndEndBefore(anyLong(), any(LocalDateTime.class), any())).thenReturn(List.of(booking));
        Collection<ResponseBookingDto> resultPast = bookingService.getAllBookingsByOwnerByState(state, userId, 0, 20);

        assertNotNull(resultPast);
        assertEquals(1, resultPast.size());

        state = BookingState.FUTURE;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(bookingRepository.findAllByItemOwnerIdAndStartAfter(anyLong(), any(LocalDateTime.class), any())).thenReturn(List.of(booking));
        Collection<ResponseBookingDto> resultFuture = bookingService.getAllBookingsByOwnerByState(state, userId, 0, 20);

        assertNotNull(resultFuture);
        assertEquals(1, resultFuture.size());

        state = BookingState.WAITING;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(bookingRepository.findAllByItemOwnerIdAndStatus(anyLong(), any(), any())).thenReturn(List.of(booking));
        Collection<ResponseBookingDto> resultWaiting = bookingService.getAllBookingsByOwnerByState(state, userId, 0, 20);

        assertNotNull(resultWaiting);
        assertEquals(1, resultWaiting.size());

        state = BookingState.REJECTED;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(bookingRepository.findAllByItemOwnerIdAndStatus(anyLong(), any(), any())).thenReturn(List.of(booking));
        Collection<ResponseBookingDto> resultRejected = bookingService.getAllBookingsByOwnerByState(state, userId, 0, 20);

        assertNotNull(resultRejected);
        assertEquals(1, resultRejected.size());
    }

    @Test
    void getAllBookingsByOwnerByStateWithUnsupportedStatusTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        UnsupportedStatusException exception = assertThrows(UnsupportedStatusException.class,
                () -> bookingService.getAllBookingsByOwnerByState(BookingState.UNSUPPORTED_STATUS, user2.getId(), 0, 20));

        assertEquals(UNSUPPORTED_STATUS, exception.getMessage());
    }

    @Test
    void getAllBookingsByOwnerByStateWithWrongUserIdTest() {
        Long userId = 999L;
        BookingState state = BookingState.ALL;

        assertThrows(NotFoundException.class, () -> bookingService.getAllBookingsByOwnerByState(state, userId, 0, 20));
    }
}