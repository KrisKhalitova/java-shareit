package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
@NoArgsConstructor
@EqualsAndHashCode
public class CommentDto {

    private String text;
    private LocalDateTime created;
    private UserDto author;
    private ItemDto item;
}
