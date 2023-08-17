//package ru.practicum.shareit.item.service;
//
//import lombok.RequiredArgsConstructor;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//import ru.practicum.shareit.item.dto.ItemDto;
//import ru.practicum.shareit.item.dto.ItemRequestDto;
//import ru.practicum.shareit.item.dto.ResponseItemDto;
//import ru.practicum.shareit.item.mapper.ItemMapper;
//import ru.practicum.shareit.item.model.Item;
//import ru.practicum.shareit.request.dto.PostItemRequestDto;
//import ru.practicum.shareit.request.mapper.ItemRequestMapper;
//import ru.practicum.shareit.request.model.ItemRequest;
//import ru.practicum.shareit.request.service.ItemRequestService;
//import ru.practicum.shareit.user.dto.UserDto;
//import ru.practicum.shareit.user.mapper.UserMapper;
//import ru.practicum.shareit.user.model.User;
//import ru.practicum.shareit.user.service.UserService;
//
//import java.util.Collection;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@Transactional
//@SpringBootTest(properties = {"db.name=test"})
//@RequiredArgsConstructor(onConstructor_ = @Autowired)
//public class ItemServiceIntegrationTest {
//    private final UserService userService;
//    private final ItemService itemService;
//    private final ItemRequestService itemRequestService;
//    private UserDto userDto1;
//    private UserDto userDto2;
//    private ItemDto itemDto1;
//    private ItemDto itemDto2;
//    private ItemRequestDto itemRequestDto;
//
//    @BeforeEach
//    void beforeEach() {
//        userDto1 = UserDto.builder()
//                .id(1L)
//                .name("user1")
//                .email("user1@mail.ru")
//                .build();
//        userDto2 = UserDto.builder()
//                .id(2L)
//                .name("user2")
//                .email("user2r@mail.ru")
//                .build();
//        itemRequestDto = ItemRequestDto.builder()
//                .id(1L)
//                .name("Itemrequest")
//                .description("Item request description")
//                .available(true)
//                .requestId(1L)
//                .build();
//        itemDto1 = ItemDto.builder()
//                .id(1L)
//                .name("itemName")
//                .description("Item description")
//                .available(true)
//                .owner(null)
//                .requestId(null)
//                .build();
//        itemDto2 = ItemDto.builder()
//                .id(2L)
//                .name("itemName2")
//                .description("Item description2")
//                .available(true)
//                .owner(null)
//                .requestId(1L)
//                .build();
//    }
//
//    @Test
//    void getAllItemsByUserId() {
//        Item item1 = ItemMapper.toItem(itemDto1);
//        Item item2 = ItemMapper.toItem(itemDto2);
//        User user1 = UserMapper.toUser(userDto1);
//        User user2 = UserMapper.toUser(userDto2);
//        PostItemRequestDto postItemRequestDto = new PostItemRequestDto(itemRequestDto.getDescription());
//        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(postItemRequestDto, user2);
//
//        userService.createNewUser(userDto1);
//        user1.setId(1L);
//        userService.createNewUser(userDto2);
//        user2.setId(2L);
//        itemService.addNewItem(itemDto1, userDto1.getId());
//        itemRequestService.createNewItemRequest(postItemRequestDto, 2L);
//
//        itemService.addNewItem(itemDto2, userDto1.getId());
//
//        item1.setOwner(user1);
//        item2.setOwner(user1);
//        item2.setItemRequest(itemRequest);
//        itemRequest.setRequester(user2);
//
//        List<ItemDto> expectedItems = List.of(itemDto1, itemDto2);
//        Collection<ResponseItemDto> actualItems = itemService.getAllItemsByUserId(userDto1.getId());
//
//        assertEquals(expectedItems.size(), actualItems.size());
//        assertEquals(2, actualItems.size());
//
//        userService.deleteUser(user1.getId());
//        userService.deleteUser(user2.getId());
//    }
//}
