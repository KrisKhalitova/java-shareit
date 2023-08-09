package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.PostItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRequestMapper {
    public static ItemRequest toItemRequest(PostItemRequestDto postItemRequestDto, User requester) {
        return ItemRequest.builder()
                .description(postItemRequestDto.getDescription())
                .created(LocalDateTime.now())
                .requester(requester)
                .build();
    }

    public static ResponseItemRequestDto toResponseItemRequestDto(ItemRequest request) {
        return ResponseItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .requesterId(request.getRequester().getId())
                .build();
    }

    public static ResponseItemRequestDto toResponseItemRequestDto(ItemRequest request, List<Item> items) {
        return ResponseItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .requesterId(request.getRequester().getId())
                .items(ItemMapper.toItemListForRequestDto(items))
                .build();
    }
}
