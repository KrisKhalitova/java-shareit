package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDtoWithBookers;
import ru.practicum.shareit.booking.dto.PostBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BookingMapperTest {

    private Booking booking;
    private ResponseBookingDto responseBookingDto;
    private BookingDtoWithBookers bookingDtoWithBookers;
    private PostBookingDto postBookingDto;
    private UserDto userDto;
    private User user;
    private Item item;
    private final LocalDateTime start = LocalDateTime.now().plusDays(3);
    private final LocalDateTime end = start.plusDays(1);

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "username", "user@mail.ru");
        item = new Item(1L, "item", "description to Item", true, user, null);
        booking = new Booking(1L, start, end, item, user, BookingStatus.WAITING);
        userDto = new UserDto(1L, user.getName(), user.getEmail());
        postBookingDto = new PostBookingDto(1L, item.getId(), start, end);
    }

    @Test
    void postBookingDtoToBookingTest() {
        booking = BookingMapper.toBooking(postBookingDto, item, user);

        assertThat(booking.getId()).isEqualTo(1L);
        assertThat(booking.getItem()).isEqualTo(item);
        assertThat(booking.getBooker()).isEqualTo(user);
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(booking.getStart()).isEqualTo(start);
        assertThat(booking.getEnd()).isEqualTo(end);
    }

    @Test
    void bookingToResponseBookingDtoTest() {
        responseBookingDto = BookingMapper.toResponseBookingDto(booking);

        assertThat(responseBookingDto.getId()).isEqualTo(1L);
        assertThat(responseBookingDto.getStart()).isEqualTo(start);
        assertThat(responseBookingDto.getEnd()).isEqualTo(end);
        assertThat(responseBookingDto.getItem().getId()).isEqualTo(item.getId());
        assertThat(responseBookingDto.getBooker()).isEqualTo(userDto);
    }

    @Test
    void bookingToNullResponseBookingDtoTest() {
        booking = null;
        responseBookingDto = BookingMapper.toResponseBookingDto(booking);

        assertNull(responseBookingDto);
    }

    @Test
    void bookingToBookingDtoWithBookerIdTest() {
        bookingDtoWithBookers = BookingMapper.toBookingDtoWithBookerId(booking);

        assertThat(bookingDtoWithBookers.getId()).isEqualTo(1L);
        assertThat(bookingDtoWithBookers.getStart()).isEqualTo(start);
        assertThat(bookingDtoWithBookers.getEnd()).isEqualTo(end);
        assertThat(bookingDtoWithBookers.getItem().getId()).isEqualTo(item.getId());
        assertThat(bookingDtoWithBookers.getBookerId()).isEqualTo(1L);
    }

    @Test
    void bookingToNullBookingDtoWithBookerIdTest() {
        booking = null;
        bookingDtoWithBookers = BookingMapper.toBookingDtoWithBookerId(booking);

        assertNull(bookingDtoWithBookers);
    }
}
