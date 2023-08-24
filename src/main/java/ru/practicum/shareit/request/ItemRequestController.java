package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.PostItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestListDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    public static final String DEFAULT_FROM_VALUE = "0";
    public static final String DEFAULT_SIZE_VALUE = "20";

    @PostMapping
    public ResponseItemRequestDto createNewItemRequest(@RequestHeader(USER_ID_HEADER) Long requesterId,
                                                       @RequestBody @Valid PostItemRequestDto postItemRequestDto) {
        ResponseItemRequestDto itemRequestDto = itemRequestService.createNewItemRequest(postItemRequestDto, requesterId);
        log.info("Создан запрос на поиск определенной вещи.");
        return itemRequestDto;
    }

    @GetMapping
    public ResponseItemRequestListDto getAllOwnerRequests(@RequestHeader(USER_ID_HEADER) @Positive Long ownerId,
                                                          @RequestParam(defaultValue = DEFAULT_FROM_VALUE) @PositiveOrZero int from,
                                                          @RequestParam(defaultValue = DEFAULT_SIZE_VALUE) @Positive int size) {
        log.info("Получен запрос на получение всех запросов владельца {}.", ownerId);
        return itemRequestService.getAllOwnerRequests(from, size, ownerId);
    }

    @GetMapping("/all")
    public ResponseItemRequestListDto getAllRequesterRequests(@RequestHeader(USER_ID_HEADER) @Positive Long requesterId,
                                                              @RequestParam(defaultValue = DEFAULT_FROM_VALUE) @PositiveOrZero int from,
                                                              @RequestParam(defaultValue = DEFAULT_SIZE_VALUE) @Positive int size) {
        log.info("Получен запрос на получение всех запросов пользователя {}.", requesterId);
        return itemRequestService.getAllRequesterRequests(from, size, requesterId);
    }

    @GetMapping("/{requestId}")
    public ResponseItemRequestDto getItemRequestById(@PathVariable Long requestId,
                                                     @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Получен запрос на получение запросов пользователя {}", userId);
        return itemRequestService.getItemRequestById(requestId, userId);
    }
}
