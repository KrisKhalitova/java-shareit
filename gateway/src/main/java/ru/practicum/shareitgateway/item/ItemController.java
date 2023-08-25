package ru.practicum.shareitgateway.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitgateway.item.dto.CommentDto;
import ru.practicum.shareitgateway.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    public static final String DEFAULT_FROM_VALUE = "0";
    public static final String DEFAULT_SIZE_VALUE = "20";

    @PostMapping
    public ResponseEntity<Object> addNewItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(USER_ID_HEADER) Long userId) {
        ResponseEntity<Object> item = itemClient.addNewItem(userId, itemDto);
        log.info("Добавлена новая вещь c id {} пользователя с id {}", itemDto.getId(), userId);
        return item;
    }

    @PatchMapping("{itemId}")
    public ResponseEntity<Object> updateItemById(@RequestBody ItemDto itemDto, @PathVariable Long itemId,
                                                 @RequestHeader(USER_ID_HEADER) Long userId) {
        ResponseEntity<Object> updatedItemDto = itemClient.updateItemById(itemId, userId, itemDto);
        log.info("Информация о вещи c id {} обновлена пользователем с id {}", itemDto.getId(), userId);
        return updatedItemDto;
    }

    @GetMapping("{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable Long itemId, @RequestHeader(USER_ID_HEADER) Long userId) {
        ResponseEntity<Object> itemDtoById = itemClient.getItemById(userId, itemId);
        log.info("Запрошена информация по вещи с id {} от пользователя с id {}", itemId, userId);
        return itemDtoById;
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByUserId(@RequestHeader(USER_ID_HEADER) Long userId,
                                                      @RequestParam(defaultValue = DEFAULT_FROM_VALUE)
                                                      @PositiveOrZero int from,
                                                      @RequestParam(defaultValue = DEFAULT_SIZE_VALUE)
                                                      @Positive int size) {
        ResponseEntity<Object> allItemsByUserId = itemClient.getAllItemsByUserId(userId, from, size);
        log.info("Запрошен список вещей пользователя с id {}", userId);
        return allItemsByUserId;
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemByText(@RequestParam String text,
                                                   @RequestParam(defaultValue = DEFAULT_FROM_VALUE)
                                                   @PositiveOrZero int from,
                                                   @RequestParam(defaultValue = DEFAULT_SIZE_VALUE)
                                                   @Positive int size) {
        ResponseEntity<Object> allItemsByText = itemClient.searchItemByText(text.toLowerCase(), from, size);
        log.info("Получен список вещей по заданному тексту");
        return allItemsByText;
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addNewComment(@Valid @RequestBody CommentDto commentDto,
                                                @PathVariable Long itemId,
                                                @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Получен запрос на создание нового отзыва по вещи с id {} от пользователя с id {}", itemId, userId);
        return itemClient.addNewComment(itemId, userId, commentDto);
    }
}