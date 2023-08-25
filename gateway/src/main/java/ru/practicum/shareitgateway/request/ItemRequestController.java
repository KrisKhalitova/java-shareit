package ru.practicum.shareitgateway.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitgateway.request.dto.PostItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    public static final String DEFAULT_FROM_VALUE = "0";
    public static final String DEFAULT_SIZE_VALUE = "20";

    @PostMapping
    public ResponseEntity<Object> createNewItemRequest(@RequestHeader(USER_ID_HEADER) Long requesterId,
                                                       @RequestBody @Valid PostItemRequestDto postItemRequestDto) {
        ResponseEntity<Object> itemRequestDto = itemRequestClient.createNewItemRequest(requesterId, postItemRequestDto);
        log.info("Создан запрос на поиск определенной вещи.");
        return itemRequestDto;
    }

    @GetMapping
    public ResponseEntity<Object> getAllOwnerRequests(@RequestHeader(USER_ID_HEADER) @Positive Long ownerId,
                                                      @RequestParam(defaultValue = DEFAULT_FROM_VALUE) @PositiveOrZero int from,
                                                      @RequestParam(defaultValue = DEFAULT_SIZE_VALUE) @Positive int size) {
        log.info("Получен запрос на получение всех запросов владельца {}.", ownerId);
        return itemRequestClient.getAllOwnerRequests(ownerId, from, size);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequesterRequests(@RequestHeader(USER_ID_HEADER) @Positive Long requesterId,
                                                          @RequestParam(defaultValue = DEFAULT_FROM_VALUE) @PositiveOrZero int from,
                                                          @RequestParam(defaultValue = DEFAULT_SIZE_VALUE) @Positive int size) {
        log.info("Получен запрос на получение всех запросов пользователя {}.", requesterId);
        return itemRequestClient.getAllRequesterRequests(requesterId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@PathVariable Long requestId,
                                                     @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Получен запрос на получение запросов пользователя {}", userId);
        return itemRequestClient.getItemRequestById(userId, requestId);
    }
}
