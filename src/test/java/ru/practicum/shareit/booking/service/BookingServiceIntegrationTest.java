package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.PostBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(properties = {"db.name=test"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIntegrationTest {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;
    private PostBookingDto postBookingDto;
    private User user1;
    private User user2;
    private final LocalDateTime start = LocalDateTime.now().plusDays(3);
    private final LocalDateTime end = start.plusDays(1);

    @BeforeEach
    void beforeEach() {
        UserDto userDto1 = UserDto.builder()
                .id(1L)
                .name("user1")
                .email("user1@mail.ru")
                .build();

        UserDto savedUserDto1 = userService.createNewUser(userDto1);
        user1 = UserMapper.toUser(savedUserDto1);
        user1.setId(1L);

        UserDto userDto2 = UserDto.builder()
                .id(2L)
                .name("user2")
                .email("user2r@mail.ru")
                .build();

        UserDto savedUserDto2 = userService.createNewUser(userDto2);
        user2 = UserMapper.toUser(savedUserDto2);
        user2.setId(2L);

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("itemName")
                .description("Item description")
                .available(true)
                .owner(user1)
                .requestId(null)
                .build();
        ItemDto savedItemDto = itemService.addNewItem(itemDto, user1.getId());
        Item item = ItemMapper.toItem(savedItemDto);
        item.setId(1L);
        item.setOwner(user1);
        itemDto.setOwner(user1);

        Booking booking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(user2)
                .status(BookingStatus.APPROVED)
                .build();
        postBookingDto = new PostBookingDto(booking.getId(), item.getId(), start, end);
    }

    @Test
    void getAllOwnersBookingsByStateTest() {
        bookingService.addNewRequestForBooking(postBookingDto, user2.getId());
        Collection<ResponseBookingDto> bookingDtoList =
                bookingService.getAllBookingsByOwnerByState(BookingState.ALL, user1.getId(), 0, 20);

        assertEquals(1, bookingDtoList.size());
    }
}
