package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.PostItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
                .items(ItemMapper.toItemListForRequestDto(request.getItems()))
                .build();
    }

    public static List<ResponseItemRequestDto> toListRequestDtoToResponseFromListItemRequest(List<ItemRequest> itemRequests) {
        if (itemRequests == null) {
            return Collections.emptyList();
        }
        return itemRequests.stream().map(ItemRequestMapper::toResponseItemRequestDto).collect(Collectors.toList());
    }
}
