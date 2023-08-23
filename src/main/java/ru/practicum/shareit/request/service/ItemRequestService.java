package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.PostItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestListDto;

public interface ItemRequestService {
    ResponseItemRequestDto createNewItemRequest(PostItemRequestDto itemRequest, Long requesterId);

    ResponseItemRequestListDto getAllOwnerRequests(int from, int size, Long ownerId);

    ResponseItemRequestListDto getAllRequesterRequests(int from, int size, Long requesterId);

    ResponseItemRequestDto getItemRequestById(Long requestId, Long userId);
}
