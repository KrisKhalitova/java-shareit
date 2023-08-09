package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.PostItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;

import java.util.Collection;

public interface ItemRequestService {
    ResponseItemRequestDto createNewItemRequest(PostItemRequestDto itemRequest, Long requesterId);

    Collection<ResponseItemRequestDto> getAllOwnerRequests(int from, int size, Long ownerId);

    Collection<ResponseItemRequestDto> getAllRequesterRequests(int from, int size, Long requesterId);

    ResponseItemRequestDto getItemRequestById(Long requestId, Long userId);
}
