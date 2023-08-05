package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseCommentDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto addNewItem(ItemDto itemDto, Long userId);

    ItemDto updateItemById(ItemDto itemDto, Long itemId, Long userId);

    ResponseItemDto getItemById(Long itemId, Long userId);

    Collection<ResponseItemDto> getAllItemsByUserId(Long userId);

    Collection<ItemDto> searchItemByText(String lowerCase);

    ResponseCommentDto addNewComment(CommentDto commentDto, Long itemId, Long userId);
}
