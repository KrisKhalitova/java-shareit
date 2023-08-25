package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@NoArgsConstructor
public class ItemDto {

    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private UserDto owner;
    private Long requestId;
}
