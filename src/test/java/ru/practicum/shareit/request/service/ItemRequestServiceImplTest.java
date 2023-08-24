package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.PostItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestListDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;
    private ItemRequest itemRequest;
    private User user;
    private Item item;
    private final LocalDateTime time = LocalDateTime.now().plusDays(1);
    private PostItemRequestDto postItemRequestDto;
    private Set<Comment> comments = new HashSet<>();
    private Comment comment;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .id(1L)
                .name("username")
                .email("user@mail.ru")
                .build();
        itemRequest = new ItemRequest(1L, "descriptionOfItemRequest", user, time, null);
        item = new Item(1L, "item", "description to Item", true, user, itemRequest);
        postItemRequestDto = new PostItemRequestDto("description");
        comment = new Comment(1L, "textComment", item, user, LocalDateTime.now());
        comments.add(comment);
    }

    @Test
    void createNewItemRequestTest() {
        Long userId = user.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);
        ResponseItemRequestDto actualNewRequest = itemRequestService.createNewItemRequest(postItemRequestDto, userId);

        assertNotNull(actualNewRequest);
        assertEquals(itemRequest.getId(), actualNewRequest.getId());
        assertEquals(itemRequest.getDescription(), actualNewRequest.getDescription());
        verify(itemRequestRepository, times(1)).save(any());
    }

    @Test
    void createNewItemRequestWithWrongUserIdTest() {
        Long userId = 999L;

        assertThrows(NotFoundException.class, () -> itemRequestService.createNewItemRequest(postItemRequestDto, userId));
    }

    @Test
    void getAllOwnerRequestsTest() {
        Long userId = user.getId();
        item.setItemRequest(itemRequest);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequesterId(any(Pageable.class), anyLong()))
                .thenReturn(List.of(itemRequest));
        when(commentRepository.findByItemIdIn(any())).thenReturn(List.of(comment));

        ResponseItemRequestListDto response = itemRequestService.getAllOwnerRequests(0, 20, userId);

        assertNotNull(response);
        assertEquals(1, response.getRequests().size());
    }

    @Test
    void getAllOwnerRequestsWithWrongUserIdTest() {
        Long userId = 25L;
        item.setItemRequest(itemRequest);

        assertThrows(NotFoundException.class, () -> itemRequestService.getAllOwnerRequests(0, 20, userId));
    }

    @Test
    void getAllRequesterRequestsTest() {
        Long userId = user.getId();
        item.setItemRequest(itemRequest);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequesterIdNot(any(Pageable.class), anyLong()))
                .thenReturn(List.of(itemRequest));
        when(commentRepository.findByItemIdIn(any())).thenReturn(List.of(comment));

        ResponseItemRequestListDto response = itemRequestService.getAllRequesterRequests(0, 20, userId);

        assertNotNull(response);
        assertEquals(1, response.getRequests().size());
    }

    @Test
    void getAllRequesterRequestsWithWrongUserIdTest() {
        Long userId = 25L;
        item.setItemRequest(itemRequest);

        assertThrows(NotFoundException.class, () -> itemRequestService.getAllRequesterRequests(0, 20, userId));
    }

    @Test
    void getItemRequestById() {
        Long userId = user.getId();
        Long requestId = 1L;
        item.setItemRequest(itemRequest);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByItemRequest(itemRequest)).thenReturn(List.of(item));

        ResponseItemRequestDto actual = itemRequestService.getItemRequestById(requestId, userId);

        assertNotNull(actual);
        assertEquals(itemRequest.getDescription(), actual.getDescription());
    }

    @Test
    void getItemRequestByWrongUserId() {
        Long userId = 25L;
        Long requestId = 1L;

        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequestById(requestId, userId));
    }
}