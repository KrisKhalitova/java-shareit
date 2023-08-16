package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
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
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.ASC;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto addNewItem(ItemDto itemDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден"));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        if (itemDto.getRequestId() != null) {
            item.setItemRequest(itemRequestRepository.findById(itemDto.getRequestId()).orElseThrow(()
                    -> new NotFoundException("Запрос не найден.")));
            return ItemMapper.toItemRequestDto(itemRepository.save(item));
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItemById(ItemDto itemDto, Long itemId, Long userId) {
        Item updatedItem = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь не найдена"));
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден"));
        if (!updatedItem.getOwner().getId().equals(userId)) {
            log.warn("Только собственник вещи может изменять информацию");
            throw new NotFoundException("Только собственник вещи может изменять информацию");
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
                new NotFoundException("Пользователь не найден"));
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
                new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь не найдена."));
        return item;
    }

    @Override
    public Collection<ResponseItemDto> getAllItemsByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден"));
        List<Item> items = new ArrayList<>(itemRepository.findAllByOwner(user, Sort.by(ASC, "id")));
        List<ResponseItemDto> itemsDto = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            itemsDto.add(getItemById(items.get(i).getId(), userId));
        }
        return itemsDto;
    }

    @Override
    public List<ItemDto> searchItemByText(String text) {
        Collection<Item> items = itemRepository.search(text);
        if (text == null) {
            return Collections.emptyList();
        }
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(toList());
    }

    @Override
    public ResponseCommentDto addNewComment(CommentDto commentDto, Long itemId, Long userId) {
        if (!bookingRepository.existsByItemIdAndBookerIdAndEndBeforeAndStatus(itemId, userId, LocalDateTime.now(), BookingStatus.APPROVED)) {
            throw new ValidationException("Отзыв не может быть создан");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь не найдена"));
        LocalDateTime localDateTime = LocalDateTime.now();
        Comment comment = CommentMapper.toComment(commentDto, item, user, localDateTime);
        log.info("Отзыв добавлен");
        return CommentMapper.toResponseCommentDto(commentRepository.save(comment));
    }
}