package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.PostItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ResponseItemRequestDto createNewItemRequest(PostItemRequestDto itemRequestDto, Long requesterId) {
        User user = userRepository.findById(requesterId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден"));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);
        return ItemRequestMapper.toResponseItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public Collection<ResponseItemRequestDto> getAllOwnerRequests(int from, int size, Long ownerId) {
        userRepository.findById(ownerId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден"));
        Pageable pageable = PageRequest.of(from, size, Sort.by("created").descending());

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterId(pageable, ownerId);
        return itemRequests.stream().map(itemRequest -> getItemRequestById(itemRequest.getId(), ownerId)).collect(Collectors.toList());
    }

    @Override
    public Collection<ResponseItemRequestDto> getAllRequesterRequests(int from, int size, Long requesterId) {
        userRepository.findById(requesterId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден"));
        Pageable pageable = PageRequest.of(from, size, Sort.by("created").descending());

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdNot(pageable, requesterId);
        return itemRequests.stream().map(itemRequest -> getItemRequestById(itemRequest.getId(), requesterId)).collect(Collectors.toList());
    }

    @Override
    public ResponseItemRequestDto getItemRequestById(Long requestId, Long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден"));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден."));

        List<Item> items = itemRepository.findAllByItemRequest(itemRequest);

        return ItemRequestMapper.toResponseItemRequestDto(itemRequest, items);
    }
}
