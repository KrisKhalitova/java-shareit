package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ItemStorageImpl implements ItemStorage {

    private final HashMap<Long, Item> items = new HashMap<>();
    private long itemId = 1;

    @Override
    public Item addNewItem(Item item, long userId) {
        if (items.containsKey(item.getId())) {
            log.warn("Вещь {} уже существует", item);
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Вещь уже существует");
        }
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
        if (items.get(itemId) == null) {
            throw new ValidationException(HttpStatus.NOT_FOUND, "Вещь не найдена");
        }
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
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return items.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable).collect(Collectors.toList());
    }
}