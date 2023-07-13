package ru.practicum.shareit.user.model;

import lombok.*;

import javax.validation.constraints.Email;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
public class User {
    private Long id;
    private String name;
    @Email
    private String email;
}
