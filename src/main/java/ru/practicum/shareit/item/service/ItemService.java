package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto addNewItem(ItemDto itemDto, long userId);

    ItemDto updateItemById(ItemDto itemDto, long itemId, long userId);

    ItemDto getItemById(long itemId);

    Collection<ItemDto> getAllItemsByUserId(long userId);

    Collection<ItemDto> searchItemByText(String lowerCase);
}
