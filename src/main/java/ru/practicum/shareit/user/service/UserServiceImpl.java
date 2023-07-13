package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public UserDto createNewUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userStorage.createNewUser(user));
    }

    @Override
    public UserDto updateUser(UserDto userDto, long id) {
        User user = UserMapper.toUser(userDto, id);
        return UserMapper.toUserDto(userStorage.updateUser(user));
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new ValidationException(HttpStatus.NOT_FOUND, "Пользователь не найден");
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        Collection<UserDto> usersDto = new ArrayList<>();
        for (User allUser : userStorage.getAllUsers()) {
            usersDto.add(UserMapper.toUserDto(allUser));
        }
        return usersDto;
    }

    @Override
    public void deleteUser(Long userId) {
        getUserById(userId);
        userStorage.deleteUser(userId);
    }
}
