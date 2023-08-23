package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemService itemService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    public static final String DEFAULT_FROM_VALUE = "0";
    public static final String DEFAULT_SIZE_VALUE = "20";

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
    public ResponseItemListDto getAllItemsByUserId(@RequestHeader(USER_ID_HEADER) Long userId,
                                                   @RequestParam(defaultValue = DEFAULT_FROM_VALUE)
                                                   @PositiveOrZero int from,
                                                   @RequestParam(defaultValue = DEFAULT_SIZE_VALUE)
                                                   @Positive int size) {
        ResponseItemListDto allItemsByUserId = itemService.getAllItemsByUserId(userId, from, size);
        log.info("Запрошен список вещей пользователя с id {}", userId);
        return allItemsByUserId;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemByText(@RequestParam String text,
                                          @RequestParam(defaultValue = DEFAULT_FROM_VALUE)
                                          @PositiveOrZero int from,
                                          @RequestParam(defaultValue = DEFAULT_SIZE_VALUE)
                                          @Positive int size) {
        List<ItemDto> allItemsByText = itemService.searchItemByText(text.toLowerCase(), from, size);
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