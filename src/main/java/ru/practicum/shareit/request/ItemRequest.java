package ru.practicum.shareit.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class ItemRequest {

    private Long id;
    private String description;
    private User requester;
    private LocalDateTime created;
}