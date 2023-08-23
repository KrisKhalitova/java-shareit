package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utils.ShareItPageRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @InjectMocks
    private ItemServiceImpl itemService;
    private User user1;
    private User user2;
    private Comment comment;
    private CommentDto commentDto;
    private UserDto userDto1;
    private UserDto userDto2;
    private ItemRequestDto itemRequestDto1;
    private ItemRequest itemRequest;
    private ResponseItemDto responseItemDto1;
    private Item item;
    private ItemDto itemDto;
    private final LocalDateTime created = LocalDateTime.now().plusDays(12);

    @BeforeEach
    void beforeEach() {
        user1 = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@mail.ru")
                .build();
        userDto1 = UserDto.builder()
                .id(user1.getId())
                .name(user1.getName())
                .email(user1.getEmail())
                .build();
        user2 = User.builder()
                .id(2L)
                .name("user2")
                .email("user2r@mail.ru")
                .build();
        userDto2 = UserDto.builder()
                .id(user2.getId())
                .name(user2.getName())
                .email(user2.getEmail())
                .build();
        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Request description")
                .requester(user1)
                .created(created)
                .build();
        item = Item.builder()
                .id(1L)
                .name("itemName")
                .description("Item description")
                .owner(user1)
                .available(true)
                .itemRequest(itemRequest)
                .build();
        itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .requestId(item.getItemRequest().getId())
                .build();
        comment = Comment.builder()
                .id(1L)
                .text("text comment")
                .item(item)
                .author(user2)
                .created(LocalDateTime.now())
                .build();
        commentDto = CommentDto.builder()
                .text(comment.getText())
                .author(userDto2)
                .item(itemDto)
                .created(comment.getCreated())
                .build();
    }

    @Test
    void addNewItemTest() {
        Long userId = user1.getId();
        Long itemReqId = itemRequest.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(itemRequestRepository.findById(itemReqId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto actualItemDto = itemService.addNewItem(itemDto, userId);

        assertNotNull(actualItemDto);
        assertEquals(itemDto.getId(), actualItemDto.getId());
        assertEquals(itemDto.getName(), actualItemDto.getName());
        assertEquals(itemDto.getDescription(), actualItemDto.getDescription());
        assertEquals(itemDto.getRequestId(), actualItemDto.getRequestId());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    public void addNewItemWithoutRequestIdTest() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.addNewItem(itemDto, userId));
    }

    @Test
    void addNewItemWithWrongUserIdTest() {
        Long userId = 10L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.addNewItem(itemDto, userId));
    }

    @Test
    void updateItemByIdTest() {
        Long itemId = item.getId();
        Long userId = user1.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);

        userRepository.findById(userId);
        itemDto.setName("new_name");
        itemDto.setDescription("new description");
        ItemDto itemDtoNew = itemService.updateItemById(itemDto, itemId, userId);
        assertThat(itemDtoNew.getName()).isEqualTo("new_name");
        assertThat(itemDtoNew.getDescription()).isEqualTo("new description");
    }

    @Test
    void updateItemByIdWithWrongItemIdTest() {
        Long itemId = 25L;
        Long userId = 1L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.updateItemById(itemDto, itemId, userId));
    }

    @Test
    void updateItemByIdWithWrongUserIdTest() {
        Long itemId = item.getId();
        Long userId = 25L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.updateItemById(itemDto, itemId, userId));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateItemByIdWithNotEqualOwnerIdAndUserIdTest() {
        Long userId = 1L;
        Long itemId = item.getId();
        item.setOwner(user2);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));

        assertThrows(NotFoundException.class, () -> itemService.updateItemById(itemDto, itemId, userId));
    }

    @Test
    void getItemByIdTest() {
        Booking lastBooking = new Booking();
        Booking nextBooking = new Booking();
        lastBooking.setStart(LocalDateTime.now().minusDays(1));
        nextBooking.setStart(LocalDateTime.now().plusDays(1));
        lastBooking.setStatus(BookingStatus.APPROVED);
        nextBooking.setStatus(BookingStatus.APPROVED);
        item.setOwner(user1);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(anyLong())).thenReturn(List.of(comment));

        ResponseItemDto responseItemDto = itemService.getItemById(item.getId(), user1.getId());

        assertNotNull(responseItemDto);
        assertEquals(item.getName(), responseItemDto.getName());
    }

    @Test
    void getItemByIdWithWrongUserIdTest() {
        Long userId = 25L;

        assertThrows(NotFoundException.class, () -> itemService.getItemById(item.getId(), userId));
    }

    @Test
    void getItemTest() {
        Long itemId = 1L;
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        Item result = itemService.getItem(itemId, userId);

        verify(userRepository).findById(userId);
        verify(itemRepository).findById(itemId);

        assertEquals(item, result);
    }

    @Test
    public void getItemWithWrongUserIdTest() {
        Long itemId = 1L;
        Long userId = 10L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItem(itemId, userId));
    }

    @Test
    public void getItemWithWrongItemIdTest() {
        Long itemId = 20L;
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));

        assertThrows(NotFoundException.class, () -> itemService.getItem(itemId, userId));
    }

    @Test
    void getAllItemsByUserIdTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRepository.findAllByOwner(any(), any())).thenReturn(List.of(item));

        ResponseItemListDto response = itemService.getAllItemsByUserId(user1.getId(), 0, 20);

        assertNotNull(response);
        assertEquals(1, response.getItems().size());
    }

    @Test
    void getAllItemsByWrongUserIdTest() {
        long userId = 25L;

        assertThrows(NotFoundException.class, () -> itemService.getAllItemsByUserId((userId), 0, 20));
    }

    @Test
    void searchItemByTextTest() {
        List<Item> itemList = new ArrayList<>();
        itemList.add(item);

        when(itemRepository.search(anyString(), any())).thenReturn(itemList);

        List<ItemDto> expectedDtoList = List.of(ItemMapper.toItemDto(item));
        List<ItemDto> actualDtoList = itemService.searchItemByText("text", 0, 20);

        assertEquals(expectedDtoList.get(0).getId(), actualDtoList.get(0).getId());
        assertEquals(expectedDtoList.get(0).getDescription(), actualDtoList.get(0).getDescription());
        assertEquals(expectedDtoList.get(0).getName(), actualDtoList.get(0).getName());
        assertEquals(expectedDtoList.get(0).getOwner(), actualDtoList.get(0).getOwner());
    }

    @Test
    void searchItemByNullTextTest() {
        Pageable pageable = new ShareItPageRequest(0, 20);
        when(itemRepository.search(null, pageable)).thenReturn(Collections.emptyList());
        List<ItemDto> actualDtoList = itemService.searchItemByText(null, 0, 20);

        assertEquals(actualDtoList.size(), 0);
    }

    @Test
    void searchItemByBlankTextTest() {
        Pageable pageable = new ShareItPageRequest(0, 20);
        when(itemRepository.search("", pageable)).thenReturn(Collections.emptyList());
        List<ItemDto> actualDtoList = itemService.searchItemByText("", 0, 20);

        assertEquals(actualDtoList.size(), 0);
    }

    @Test
    void addNewCommentTest() {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.existsByItemIdAndBookerIdAndEndBeforeAndStatus(anyLong(), anyLong(), any(), any()))
                .thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.save(any())).thenReturn(comment);

        ResponseCommentDto responseCommentDto = itemService.addNewComment(commentDto, item.getId(), user1.getId());

        assertNotNull(responseCommentDto);
        assertEquals(comment.getText(), responseCommentDto.getText());
    }

    @Test
    void addNewCommentWithWrongBookingTest() {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.REJECTED);
        when(bookingRepository.existsByItemIdAndBookerIdAndEndBeforeAndStatus(anyLong(), anyLong(), any(), any()))
                .thenReturn(false);

        assertThrows(ValidationException.class, () -> itemService.addNewComment(commentDto,
                item.getId(), user1.getId()));
    }

    @Test
    void addNewCommentWithWrongUserIdTest() {
        Long userId = 25L;
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.existsByItemIdAndBookerIdAndEndBeforeAndStatus(anyLong(), anyLong(), any(), any()))
                .thenReturn(true);

        assertThrows(NotFoundException.class, () -> itemService.addNewComment(commentDto, item.getId(), userId));
    }

    @Test
    void addNewCommentWithWrongItemIdTest() {
        Long itemId = 25L;
        Long userId = 1L;
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.existsByItemIdAndBookerIdAndEndBeforeAndStatus(anyLong(), anyLong(), any(), any()))
                .thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));

        assertThrows(NotFoundException.class, () -> itemService.addNewComment(commentDto, itemId, userId));
    }
}