package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto addNewItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(USER_ID_HEADER) long userId) {
        ItemDto item = itemService.addNewItem(itemDto, userId);
        log.info("Добавлена новая вещь");
        return item;
    }

    @PatchMapping("{itemId}")
    public ItemDto updateItemById(@RequestBody ItemDto itemDto, @PathVariable long itemId,
                                  @RequestHeader(USER_ID_HEADER) long userId) {
        ItemDto updatedItemDto = itemService.updateItemById(itemDto, itemId, userId);
        log.info("Информация о вещи обновлена");
        return updatedItemDto;
    }

    @GetMapping("{itemId}")
    public ItemDto getItemById(@PathVariable long itemId) {
        ItemDto itemDtoById = itemService.getItemById(itemId);
        log.info("Получена информация по вещи с id {}", itemId);
        return itemDtoById;
    }

    @GetMapping
    public Collection<ItemDto> getAllItemsByUserId(@RequestHeader(USER_ID_HEADER) long userId) {
        Collection<ItemDto> allItemsByUserId = itemService.getAllItemsByUserId(userId);
        log.info("Получен список вещей пользователя с id {}", userId);
        return allItemsByUserId;
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItemByText(@RequestParam String text) {
        Collection<ItemDto> allItemsByText = itemService.searchItemByText(text.toLowerCase());
        log.info("Получен список вещей по заданному тексту");
        return allItemsByText;
    }
}
