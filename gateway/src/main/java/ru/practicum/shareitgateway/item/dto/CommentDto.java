package ru.practicum.shareitgateway.item.dto;

import lombok.*;
import ru.practicum.shareitgateway.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
@NoArgsConstructor
@EqualsAndHashCode
public class CommentDto {

    @NotBlank
    private String text;
    private LocalDateTime created;
    private UserDto author;
    private ItemDto item;
}
