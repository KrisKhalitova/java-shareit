package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ItemMapperTest {

    private ItemDto itemDto;
    private Item item;
    private Booking bookingLast;
    private Booking bookingNext;
    private List<Item> items = new ArrayList<>();
    private final LocalDateTime startLast = LocalDateTime.now().plusDays(3);
    private final LocalDateTime endLast = startLast.plusDays(1);
    private final LocalDateTime startNext = LocalDateTime.now().plusDays(10);
    private final LocalDateTime endNext = startNext.plusDays(1);
    private final LocalDateTime created = LocalDateTime.now().plusDays(1);
    private Comment comment;

    @BeforeEach
    void beforeEach() {
        User user = new User(1L, "username", "user@mail.ru");
        ItemRequest itemRequest = new ItemRequest(1L, "description to request1", user, created);
        item = new Item(1L, "item1", "description to Item1", true, user, itemRequest);
        bookingLast = new Booking(1L, startLast, endLast, item, user, BookingStatus.WAITING);
        bookingNext = new Booking(2L, startNext, endNext, item, user, BookingStatus.WAITING);
        comment = new Comment(1L, "text", item, user, created);
        itemDto = new ItemDto(1L, item.getName(), item.getDescription(), item.getAvailable(), user, item, item.getItemRequest().getId());
    }

    @Test
    void itemDtoToItemTest() {
        item = ItemMapper.toItem(itemDto);

        assertThat(item.getId()).isEqualTo(itemDto.getId());
        assertThat(item.getName()).isEqualTo(itemDto.getName());
        assertThat(item.getDescription()).isEqualTo(itemDto.getDescription());
        assertThat(item.getAvailable()).isEqualTo(itemDto.getAvailable());
    }

    @Test
    void itemToItemDtoTest() {
        itemDto = ItemMapper.toItemDto(item);

        assertThat(itemDto.getId()).isEqualTo(item.getId());
        assertThat(itemDto.getName()).isEqualTo(item.getName());
        assertThat(itemDto.getDescription()).isEqualTo(item.getDescription());
        assertThat(itemDto.getAvailable()).isEqualTo(item.getAvailable());
    }

    @Test
    void itemToItemRequestDtoTest() {
        itemDto = ItemMapper.toItemRequestDto(item);

        assertThat(itemDto.getId()).isEqualTo(item.getId());
        assertThat(itemDto.getName()).isEqualTo(item.getName());
        assertThat(itemDto.getDescription()).isEqualTo(item.getDescription());
        assertThat(itemDto.getAvailable()).isEqualTo(item.getAvailable());
        assertThat(itemDto.getRequestId()).isEqualTo(item.getItemRequest().getId());
    }

    @Test
    void itemToResponseItemDtoTest() {
        Collection<Comment> comments = new ArrayList<>();
        comments.add(comment);
        ResponseItemDto responseItemDto = ItemMapper.toResponseItemDto(item, bookingLast, bookingNext, comments);

        assertThat(responseItemDto.getId()).isEqualTo(item.getId());
        assertThat(responseItemDto.getLastBooking().getStart()).isEqualTo(bookingLast.getStart());
        assertThat(responseItemDto.getNextBooking().getStart()).isEqualTo(bookingNext.getStart());
        assertThat(responseItemDto.getRequestId()).isEqualTo(item.getItemRequest().getId());
        assertThat(responseItemDto.getOwner().getName()).isEqualTo(item.getOwner().getName());
        assertThat(responseItemDto.getComments().get(0).getText()).isEqualTo(comment.getText());
    }

    @Test
    void itemToItemForRequestDtoTest() {
        ItemRequestDto itemRequestDto = ItemMapper.toItemForRequestDto(item);

        assertThat(itemRequestDto.getId()).isEqualTo(item.getId());
        assertThat(itemRequestDto.getName()).isEqualTo(item.getName());
        assertThat(itemRequestDto.getDescription()).isEqualTo(item.getDescription());
        assertThat(itemRequestDto.getAvailable()).isEqualTo(item.getAvailable());
        assertThat(itemRequestDto.getRequestId()).isEqualTo(item.getItemRequest().getId());
    }

    @Test
    void itemsListToItemRequestDtoListTest() {
        items.add(item);
        List<ItemRequestDto> itemItemRequestDtoList = ItemMapper.toItemListForRequestDto(items);

        assertThat(itemItemRequestDtoList.size()).isEqualTo(1);
        assertThat(itemItemRequestDtoList.get(0).getName()).isEqualTo(item.getName());
    }

    @Test
    void itemsNullListToItemRequestDtoListTest() {
        items = null;
        List<ItemRequestDto> itemItemRequestDtoList = ItemMapper.toItemListForRequestDto(items);

        assertNull(itemItemRequestDtoList);
    }
}
