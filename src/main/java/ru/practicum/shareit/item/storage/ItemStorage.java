package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {
    Item addNewItem(Item item, long userId);

    Item updateItemById(Item item, long itemId, long userId);

    Item getItemById(long itemId);

    Collection<Item> getAllItemsByUserId(long userId);

    Collection<Item> searchItemByText(String text);
}
