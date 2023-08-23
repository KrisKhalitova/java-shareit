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
    private List<ResponseItemRequestDto> responseList = new ArrayList<>();

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "username", "user@mail.ru");
        item = new Item(1L, "item", "description to Item", true, user, null, null);
        itemRequest = new ItemRequest(1L, "descriptionOfItemRequest", user, time, null);
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
    void itemRequestToListResponseItemRequestDto() {
        responseItemRequestDto = ItemRequestMapper.toResponseItemRequestDto(itemRequest);
        responseList.add(responseItemRequestDto);

        List<ItemRequest> itemRequests = new ArrayList<>();
        itemRequests.add(itemRequest);
        List<ResponseItemRequestDto> responseItemsRequestDto = ItemRequestMapper.toListRequestDtoToResponseFromListItemRequest(itemRequests);

        assertThat(responseItemsRequestDto.size()).isEqualTo(1);
    }

    @Test
    void itemNullRequestToListResponseItemRequestDto() {
        responseItemRequestDto = ItemRequestMapper.toResponseItemRequestDto(itemRequest);
        responseList.add(responseItemRequestDto);

        List<ItemRequest> itemRequests = null;
        List<ResponseItemRequestDto> responseItemsRequestDto = ItemRequestMapper.toListRequestDtoToResponseFromListItemRequest(itemRequests);

        assertThat(responseItemsRequestDto.isEmpty());
    }
}
