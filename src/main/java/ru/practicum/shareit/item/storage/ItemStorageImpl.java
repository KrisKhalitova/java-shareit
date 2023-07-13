package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ItemStorageImpl implements ItemStorage {

    private final HashMap<Long, Item> items = new HashMap<>();
    private long itemId = 1;
    private final UserStorage userStorage;

    @Override
    public Item addNewItem(Item item, long userId) {
        if (items.containsKey(item.getId())) {
            log.warn("Вещь {} уже существует", item);
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Вещь уже существует");
        }
        User user = userStorage.getUserById(userId);
        item.setOwner(user);
        item.setId(itemId++);
        items.put(item.getId(), item);
        log.info("Новая вещь {} добавлена", item);
        return items.get(item.getId());
    }

    @Override
    public Item updateItemById(Item item, long itemId, long userId) {
        items.put(item.getId(), item);
        log.info("Информация по вещи {} обновлена", item);
        return items.get(item.getId());
    }

    @Override
    public Item getItemById(long itemId) {
        return items.get(itemId);
    }

    @Override
    public Collection<Item> getAllItemsByUserId(long userId) {
        Collection<Item> itemList = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner().getId().equals(userId)) {
                itemList.add(item);
            }
        }
        return itemList;
    }

    @Override
    public Collection<Item> searchItemByText(String text) {
        Collection<Item> itemList = new ArrayList<>();
        for (Item item : items.values()) {
            if ((item.getName().toLowerCase().contains(text) || item.getDescription().toLowerCase().contains(text))
                    && item.getAvailable()) {
                itemList.add(item);
            }
        }
        return itemList;
    }
}
