package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class CommentDto {

    private long id;
    @NotBlank
    private String text;
    private LocalDateTime created;
    private UserDto author;
    private ItemDto item;
}
