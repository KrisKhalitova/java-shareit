package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utils.ShareItPageRequest;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    public ResponseItemListDto getAllItemsByUserId(Long userId, int from, int size) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден"));

        Pageable pageable = new ShareItPageRequest(from, size, Sort.by(ASC, "id"));
        List<Item> items = itemRepository.findAllByOwner(user, pageable);
        setComments(items);
        List<ResponseItemDto> personalItems = setBookings(items);

        return ResponseItemListDto.builder().items(personalItems).build();
    }

    @Override
    public List<ItemDto> searchItemByText(String text, int from, int size) {
        Pageable pageable = new ShareItPageRequest(from, size);
        List<Item> items = itemRepository.search(text, pageable);
        setBookings(items);
        setComments(items);
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

    @Transactional
    public void setComments(List<Item> items) {
        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        List<Comment> allComments = commentRepository.findByItemIdIn(itemIds);
        Map<Long, List<Comment>> commentsMap = allComments.stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));
        for (Item item : items) {
            List<Comment> comments = commentsMap.getOrDefault(item.getId(), Collections.emptyList());
            item.setComments(new HashSet<>(comments));
        }
    }

    @Transactional
    public List<ResponseItemDto> setBookings(List<Item> items) {
        List<ResponseItemDto> personalItems = new ArrayList<>();
        List<Booking> beforeBookings = bookingRepository.findAllByItemIdInAndStartBeforeAndStatusOrderByItemIdAscEndDesc(
                items.stream().map(Item::getId).collect(Collectors.toList()),
                LocalDateTime.now(), BookingStatus.APPROVED
        );
        List<Booking> afterBookings = bookingRepository.findAllByItemIdInAndStartAfterAndStatusOrderByItemIdAscStartAsc(
                items.stream().map(Item::getId).collect(Collectors.toList()),
                LocalDateTime.now(), BookingStatus.APPROVED
        );
        Map<Long, List<Booking>> beforeBookingsMap = beforeBookings.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));
        Map<Long, List<Booking>> afterBookingsMap = afterBookings.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        for (Item item : items) {
            ResponseItemDto itemDto = ItemMapper.toResponseItemDtoFromItem(item);

            List<Booking> lastBookings = beforeBookingsMap.get(item.getId());
            if (lastBookings != null && !lastBookings.isEmpty()) {
                Booking lastBooking = lastBookings.get(0);
                itemDto.setLastBooking(BookingMapper.toBookingDtoWithBookerId(lastBooking));
            } else {
                itemDto.setLastBooking(null);
            }

            List<Booking> nextBookings = afterBookingsMap.get(item.getId());
            if (nextBookings != null && !nextBookings.isEmpty()) {
                Booking nextBooking = nextBookings.get(0);
                itemDto.setNextBooking(BookingMapper.toBookingDtoWithBookerId(nextBooking));
            } else {
                itemDto.setNextBooking(null);
            }
            personalItems.add(itemDto);
        }
        return personalItems;
    }
}