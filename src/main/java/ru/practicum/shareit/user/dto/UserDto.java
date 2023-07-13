package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;
    @NotNull
    private String name;
    @Email
    @NotNull
    private String email;
}
