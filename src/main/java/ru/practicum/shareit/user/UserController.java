package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto createNewUser(@Valid @RequestBody UserDto userDto) {
        UserDto user = userService.createNewUser(userDto);
        log.info("Создан новый пользователь");
        return user;
    }

    @PatchMapping("{id}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable Long id) {
        UserDto updatedUserDto = userService.updateUser(userDto, id);
        log.info("Пользователь обновлён");
        return updatedUserDto;
    }

    @GetMapping("{id}")
    public UserDto getUserById(@PathVariable Long id) {
        UserDto userDtoById = userService.getUserById(id);
        log.info("Получен пользователь по {} id", id);
        return userDtoById;
    }

    @GetMapping
    public Collection<UserDto> getAllUsers() {
        Collection<UserDto> allUsers = userService.getAllUsers();
        log.info("Получен список всех пользователей");
        return allUsers;
    }

    @DeleteMapping("{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        log.info("Пользователь удалён");
    }
}
