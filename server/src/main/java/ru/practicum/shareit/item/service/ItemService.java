package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {

    ItemDto addNewItem(ItemDto itemDto, Long userId);

    ItemDto updateItemById(ItemDto itemDto, Long itemId, Long userId);

    ResponseItemDto getItemById(Long itemId, Long userId);

    ResponseItemListDto getAllItemsByUserId(Long userId, int from, int size);

    List<ItemDto> searchItemByText(String text, int from, int size);

    ResponseCommentDto addNewComment(CommentDto commentDto, Long itemId, Long userId);
}
