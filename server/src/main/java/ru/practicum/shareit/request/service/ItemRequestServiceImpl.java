package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ResponseCommentDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.PostItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestListDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utils.ShareItPageRequest;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    public static final Sort SORT_BY_CREATED_DESC = Sort.by("created").descending();

    @Override
    public ResponseItemRequestDto createNewItemRequest(PostItemRequestDto itemRequestDto, Long requesterId) {
        User user = userRepository.findById(requesterId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден"));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);
        return ItemRequestMapper.toResponseItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public ResponseItemRequestListDto getAllOwnerRequests(int from, int size, Long ownerId) {
        userRepository.findById(ownerId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден"));
        Pageable pageable = new ShareItPageRequest(from, size, SORT_BY_CREATED_DESC);

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterId(pageable, ownerId);
        List<Item> items = itemRepository.findAllByItemRequestIn(itemRequests);
        List<ResponseItemDto> responseItemDtoList = ItemMapper.toResponseItemDtoListFromListOfItems(items);
        setItems(itemRequests, items, responseItemDtoList);

        return ResponseItemRequestListDto.builder()
                .requests(ItemRequestMapper.toListRequestDtoToResponseFromListItemRequest(itemRequests)).build();
    }

    @Override
    public ResponseItemRequestListDto getAllRequesterRequests(int from, int size, Long requesterId) {
        userRepository.findById(requesterId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден"));
        Pageable pageable = new ShareItPageRequest(from, size, SORT_BY_CREATED_DESC);

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdNot(pageable, requesterId);
        List<Item> items = itemRepository.findAllByItemRequestIn(itemRequests);
        List<ResponseItemDto> responseItemDtoList = ItemMapper.toResponseItemDtoListFromListOfItems(items);
        setItems(itemRequests, items, responseItemDtoList);

        return ResponseItemRequestListDto.builder()
                .requests(ItemRequestMapper.toListRequestDtoToResponseFromListItemRequest(itemRequests)).build();
    }

    @Override
    public ResponseItemRequestDto getItemRequestById(Long requestId, Long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден"));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден."));
        List<Item> items = itemRepository.findAllByItemRequest(itemRequest);
        List<ResponseItemDto> responseItemDtoList = ItemMapper.toResponseItemDtoListFromListOfItems(items);
        setItems(List.of(itemRequest), items, responseItemDtoList);

        return ItemRequestMapper.toResponseItemRequestDto(itemRequest);
    }

    private void setItems(List<ItemRequest> itemRequests, List<Item> items, List<ResponseItemDto> responseItemDtoList) {
        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        List<Comment> comments = commentRepository.findByItemIdIn(itemIds);
        Map<Long, List<Comment>> commentsMap = comments.stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));
        for (ResponseItemDto responseItemDto : responseItemDtoList) {
            List<Comment> itemComments = commentsMap.getOrDefault(responseItemDto.getId(), Collections.emptyList());
            List<ResponseCommentDto> commentDtoList = CommentMapper.toResponseCommentDtoList(itemComments);
            responseItemDto.setComments(new ArrayList<>(commentDtoList));
        }
        for (ItemRequest itemRequest : itemRequests) {
            List<Item> requestItems = items.stream()
                    .filter(item -> itemRequest.getId().equals(item.getItemRequest().getId()))
                    .collect(Collectors.toList());
            itemRequest.setItems(new HashSet<>(requestItems));
        }
    }
}
