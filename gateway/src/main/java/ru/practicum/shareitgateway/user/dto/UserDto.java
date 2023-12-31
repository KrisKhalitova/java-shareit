package ru.practicum.shareitgateway.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class UserDto {

    private Long id;
    @NotBlank
    private String name;
    @Email
    @NotBlank
    private String email;
}
