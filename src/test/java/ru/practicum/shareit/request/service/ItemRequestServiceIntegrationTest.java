package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.PostItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(properties = {"db.name=test"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestServiceIntegrationTest {
    private final UserService userService;
    private final ItemService itemService;
    private final ItemRequestService itemRequestService;
    private UserDto userDto1;
    private UserDto userDto2;
    private ItemDto itemDto;
    private User user1;
    private User user2;

    @BeforeEach
    void beforeEach() {
        user1 = User.builder()
                .id(1L)
                .name("user1name")
                .email("user1@mail.ru")
                .build();
        userDto1 = UserDto.builder()
                .id(user1.getId())
                .name(user1.getName())
                .email(user1.getEmail())
                .build();
        userService.createNewUser(userDto1);
        user1.setId(1L);
        user2 = User.builder()
                .id(2L)
                .name("user2name")
                .email("user2@mail.ru")
                .build();
        userDto2 = UserDto.builder()
                .id(user2.getId())
                .name(user2.getName())
                .email(user2.getEmail())
                .build();
        userService.createNewUser(userDto2);
        user2.setId(2L);
        itemDto = ItemDto.builder()
                .id(2L)
                .name("itemName2")
                .description("Item description2")
                .available(true)
                .owner(null)
                .requestId(1L)
                .build();
    }

    @Test
    public void getItemRequestById() { //тест по отдельности проходит, а в общем запуске нет
        PostItemRequestDto postItemRequestDto = new PostItemRequestDto("description");
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(postItemRequestDto, user2);
        itemRequest.setId(1L);

        itemRequestService.createNewItemRequest(postItemRequestDto, 2L);

        ItemDto savedItemDto = itemService.addNewItem(itemDto, userDto1.getId());
        Item item = ItemMapper.toItem(savedItemDto);

        item.setOwner(user1);
        item.setItemRequest(itemRequest);

        List<ItemRequest> expectedItemRequests = List.of(itemRequest);

        ResponseItemRequestDto actualItemRequest = itemRequestService.getItemRequestById(1L, 2L);

        assertEquals(1, expectedItemRequests.size());
        assertEquals(expectedItemRequests.get(0).getId(), actualItemRequest.getId());
    }
}
