package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto addNewItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(userIdHeader) long userId) {
        return itemService.addNewItem(itemDto, userId);
    }

    @PatchMapping("{itemId}")
    public ItemDto updateItemById(@RequestBody ItemDto itemDto, @PathVariable long itemId,
                                  @RequestHeader(userIdHeader) long userId) {
        return itemService.updateItemById(itemDto, itemId, userId);
    }

    @GetMapping("{itemId}")
    public ItemDto getItemById(@PathVariable long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public Collection<ItemDto> getAllItemsByUserId(@RequestHeader(userIdHeader) long userId) {
        return itemService.getAllItemsByUserId(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItemByText(@RequestParam String text) {
        return itemService.searchItemByText(text.toLowerCase());
    }
}
