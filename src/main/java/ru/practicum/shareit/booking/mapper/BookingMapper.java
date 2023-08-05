package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDtoWithBookers;
import ru.practicum.shareit.booking.dto.PostBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    public static Booking toBooking(PostBookingDto postBookingDto, Item item, User booker) {
        return Booking.builder()
                .start(postBookingDto.getStart())
                .end(postBookingDto.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
    }

    public static ResponseBookingDto toResponseBookingDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return ResponseBookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(UserMapper.toUserDto(booking.getBooker()))
                .item(ItemMapper.toItemDto(booking.getItem()))
                .status(booking.getStatus())
                .build();
    }

    public static BookingDtoWithBookers toBookingDtoWithBookerId(Booking booking) {
        if (booking == null) {
            return null;
        }
        return BookingDtoWithBookers.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookerId(UserMapper.toUserDto(booking.getBooker()).getId())
                .item(ItemMapper.toItemDto(booking.getItem()))
                .status(booking.getStatus())
                .build();
    }
}