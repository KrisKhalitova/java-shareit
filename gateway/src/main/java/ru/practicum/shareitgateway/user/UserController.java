package ru.practicum.shareitgateway.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitgateway.user.dto.UserDto;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createNewUser(@Valid @RequestBody UserDto userDto) {
        ResponseEntity<Object> user = userClient.createNewUser(userDto);
        log.info("Создан новый пользователь");
        return user;
    }

    @PatchMapping("{id}")
    public ResponseEntity<Object> updateUser(@RequestBody UserDto userDto, @PathVariable Long id) {
        ResponseEntity<Object> updatedUserDto = userClient.updateUser(id, userDto);
        log.info("Пользователь обновлён");
        return updatedUserDto;
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        ResponseEntity<Object> userDtoById = userClient.getUserById(id);
        log.info("Получен пользователь по {} id", id);
        return userDtoById;
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        ResponseEntity<Object> allUsers = userClient.getAllUsers();
        log.info("Получен список всех пользователей");
        return allUsers;
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        log.info("Пользователь удалён");
        return userClient.deleteUser(id);
    }
}
