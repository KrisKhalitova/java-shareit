package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.PostItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ItemRequestMapperTest {

    private ItemRequest itemRequest;
    private User user;
    private Item item;
    private ResponseItemRequestDto responseItemRequestDto;
    private final LocalDateTime time = LocalDateTime.now().plusDays(1);

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "username", "user@mail.ru");
        item = new Item(1L, "item", "description to Item", true, user, null);
        itemRequest = new ItemRequest(1L, "descriptionOfItemRequest", user, time);
    }

    @Test
    void postItemRequestDtoToItemRequestTest() {
        PostItemRequestDto postItemRequestDto = new PostItemRequestDto("description");
        itemRequest = ItemRequestMapper.toItemRequest(postItemRequestDto, user);

        assertThat(itemRequest.getDescription()).isEqualTo(postItemRequestDto.getDescription());
    }

    @Test
    void itemRequestToResponseItemRequestTest() {
        responseItemRequestDto = ItemRequestMapper.toResponseItemRequestDto(itemRequest);

        assertThat(responseItemRequestDto.getId()).isEqualTo(itemRequest.getId());
        assertThat(responseItemRequestDto.getDescription()).isEqualTo(itemRequest.getDescription());
        assertThat(responseItemRequestDto.getCreated()).isEqualTo(itemRequest.getCreated());
        assertThat(responseItemRequestDto.getRequesterId()).isEqualTo(itemRequest.getRequester().getId());
    }

    @Test
    void itemRequestAndListToResponseItemRequestDto() {
        List<Item> items = new ArrayList<>();
        items.add(item);
        item.setItemRequest(itemRequest);
        responseItemRequestDto = ItemRequestMapper.toResponseItemRequestDto(itemRequest, items);

        assertThat(responseItemRequestDto.getDescription()).isEqualTo(itemRequest.getDescription());
        assertThat(responseItemRequestDto.getCreated()).isEqualTo(itemRequest.getCreated());
        assertThat(responseItemRequestDto.getRequesterId()).isEqualTo(itemRequest.getRequester().getId());
        assertThat(responseItemRequestDto.getItems().get(0).getName()).isEqualTo(item.getName());
    }
}
