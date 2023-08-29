package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto createNewUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, long id);

    UserDto getUserById(Long userId);

    Collection<UserDto> getAllUsers();

    void deleteUser(Long userId);
}
