package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseCommentDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemService itemService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto addNewItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(USER_ID_HEADER) Long userId) {
        ItemDto item = itemService.addNewItem(itemDto, userId);
        log.info("Добавлена новая вещь c id {} пользователя с id {}", itemDto.getId(), userId);
        return item;
    }

    @PatchMapping("{itemId}")
    public ItemDto updateItemById(@RequestBody ItemDto itemDto, @PathVariable Long itemId,
                                  @RequestHeader(USER_ID_HEADER) Long userId) {
        ItemDto updatedItemDto = itemService.updateItemById(itemDto, itemId, userId);
        log.info("Информация о вещи c id {} обновлена пользователем с id {}", itemDto.getId(), userId);
        return updatedItemDto;
    }

    @GetMapping("{itemId}")
    public ResponseItemDto getItemById(@PathVariable Long itemId, @RequestHeader(USER_ID_HEADER) Long userId) {
        ResponseItemDto itemDtoById = itemService.getItemById(itemId, userId);
        log.info("Запрошена информация по вещи с id {} от пользователя с id {}", itemId, userId);
        return itemDtoById;
    }

    @GetMapping
    public Collection<ResponseItemDto> getAllItemsByUserId(@RequestHeader(USER_ID_HEADER) Long userId) {
        Collection<ResponseItemDto> allItemsByUserId = itemService.getAllItemsByUserId(userId);
        log.info("Запрошен список вещей пользователя с id {}", userId);
        return allItemsByUserId;
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItemByText(@RequestParam String text) {
        Collection<ItemDto> allItemsByText = itemService.searchItemByText(text.toLowerCase());
        log.info("Получен список вещей по заданному тексту");
        return allItemsByText;
    }

    @PostMapping("/{itemId}/comment")
    public ResponseCommentDto addNewComment(@Valid @RequestBody CommentDto commentDto,
                                            @PathVariable Long itemId,
                                            @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Получен запрос на создание нового отзыва по вещи с id {} от пользователя с id {}", itemId, userId);
        return itemService.addNewComment(commentDto, itemId, userId);
    }
}