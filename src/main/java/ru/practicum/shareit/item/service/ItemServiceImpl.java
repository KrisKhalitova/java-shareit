package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseCommentDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto addNewItem(ItemDto itemDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ValidationException(HttpStatus.NOT_FOUND, "Пользователь не найден"));
        user.setId(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItemById(ItemDto itemDto, Long itemId, Long userId) {
        Item updatedItem = itemRepository.findById(itemId).orElseThrow(() ->
                new ValidationException(HttpStatus.NOT_FOUND, "Вещь не найдена"));
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ValidationException(HttpStatus.NOT_FOUND, "Пользователь не найден"));
        if (!updatedItem.getOwner().getId().equals(userId) || user == null) {
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
        return ItemMapper.toItemDto(itemRepository.save(updatedItem));
    }

    @Override
    public ResponseItemDto getItemById(Long itemId, Long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new ValidationException(HttpStatus.NOT_FOUND, "Пользователь не найден"));
        Item item = getItem(itemId, userId);
        LocalDateTime now = LocalDateTime.now();
        Collection<Comment> comments = commentRepository.findByItemId(itemId);
        Booking lastBooking = null;
        Booking nextBooking = null;
        if (item.getOwner().getId().equals(userId)) {
            lastBooking = bookingRepository.findBookingByItemIdAndStartBefore(item.getId(), now, Sort.by("start").descending()).stream().findFirst().orElse(null);
            nextBooking = bookingRepository.findBookingByItemIdAndStartAfterAndStatus(item.getId(), now, BookingStatus.APPROVED, Sort.by("start")).stream().findFirst().orElse(null);
        }
        return ItemMapper.toResponseItemDto(item, lastBooking, nextBooking, comments);
    }

    public Item getItem(Long itemId, Long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new ValidationException(HttpStatus.NOT_FOUND, "Пользователь не найден"));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ValidationException(HttpStatus.NOT_FOUND, "Пользователь не найден."));
        return item;
    }

    @Override
    public Collection<ResponseItemDto> getAllItemsByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ValidationException(HttpStatus.NOT_FOUND, "Пользователь не найден"));
        List<Item> items = new ArrayList<>(itemRepository.findAllByOwnerOrderById(user));
        List<ResponseItemDto> itemsDto = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            itemsDto.add(getItemById(items.get(i).getId(), userId));
        }
        return itemsDto;
    }

    @Override
    public Collection<ItemDto> searchItemByText(String text) {
        Collection<Item> items = itemRepository.search(text);
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ResponseCommentDto addNewComment(CommentDto commentDto, Long itemId, Long userId) {
        Comment comment = Comment.builder()
                .text(commentDto.getText())
                .build();
        comment.setItem(itemRepository.findById(itemId).orElseThrow(() ->
                new ValidationException(HttpStatus.NOT_FOUND, "Вещь не найдена")));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException(HttpStatus.NOT_FOUND, "Пользователь не найден"));
        comment.setAuthor(user);

        if (!bookingRepository.existsByBookerIdAndEndBeforeAndStatus(userId, LocalDateTime.now(), BookingStatus.APPROVED)) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Отзыв не может быть создан");
        }
        comment.setCreated(LocalDateTime.now());
        log.info("Отзыв добавлен");
        return CommentMapper.toResponseCommentDto(commentRepository.save(comment));
    }
}
