package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto addNewItem(ItemDto itemDto, long userId) {
        User user = userStorage.getUserById(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        return ItemMapper.toItemDto(itemStorage.addNewItem(item, userId));
    }

    @Override
    public ItemDto updateItemById(ItemDto itemDto, long itemId, long userId) {
        Item updatedItem = itemStorage.getItemById(itemId);
        User user = userStorage.getUserById(userId);
        if (updatedItem.getOwner().getId() != userId || user == null) {
            log.warn("Только собственник вещи может изменять информацию");
            throw new ValidationException(HttpStatus.NOT_FOUND, "Только собственник вещи может изменять информацию");
        }
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            updatedItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            updatedItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            updatedItem.setAvailable(itemDto.getAvailable());
        }
        log.info("Вещь с id = {} обновлена", itemId);
        return ItemMapper.toItemDto(itemStorage.updateItemById(updatedItem, itemId, userId));
    }

    @Override
    public ItemDto getItemById(long itemId) {
        Item item = itemStorage.getItemById(itemId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public Collection<ItemDto> getAllItemsByUserId(long userId) {
        userStorage.getUserById(userId);
        Collection<ItemDto> itemsDto = new ArrayList<>();
        for (Item allItem : itemStorage.getAllItemsByUserId(userId)) {
            itemsDto.add(ItemMapper.toItemDto(allItem));
        }
        return itemsDto;
    }

    @Override
    public Collection<ItemDto> searchItemByText(String text) {
        Collection<Item> items = itemStorage.searchItemByText(text);
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}