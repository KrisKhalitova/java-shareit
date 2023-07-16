package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.EmailException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;

@Repository
@Slf4j
public class UserStorageImpl implements UserStorage {
    private final HashMap<Long, User> users = new HashMap<>();
    private long userId = 1;


    @Override
    public User createNewUser(User user) {
        if (users.containsKey(user.getId())) {
            log.warn("Пользователь {} уже существует", user);
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Пользователь уже существует");
        }
        if (checkEmail(user.getEmail())) {
            throw new EmailException(HttpStatus.BAD_REQUEST, "Email уже существует и не может дублироваться");
        }
        user.setId(userId++);
        users.put(user.getId(), user);
        log.info("Новый пользователь {} добавлен", user);
        return users.get(user.getId());
    }

    @Override
    public User updateUser(User userUpdated, long id) {
        User user = users.get(id);
        if (userUpdated.getName() != null && !userUpdated.getName().isEmpty()) {
            user.setName(userUpdated.getName());
        }
        if (userUpdated.getEmail() != null && !userUpdated.getEmail().isEmpty()) {
            if (checkEmail(userUpdated.getEmail())) {
                if (!userUpdated.getEmail().equals(user.getEmail())) {
                    throw new EmailException(HttpStatus.BAD_REQUEST, "Email уже существует и не может дублироваться");
                }
            }
            user.setEmail(userUpdated.getEmail());
            users.put(user.getId(), user);
            log.info("Пользователь {} обновлен", user);
        }
        return users.get(user.getId());
    }

    @Override
    public User getUserById(Long userId) {
        if (users.get(userId) == null) {
            throw new ValidationException(HttpStatus.NOT_FOUND, "Пользователь не найден");
        }
        return users.get(userId);
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public void deleteUser(Long userId) {
        users.remove(userId);
        log.info("Пользователь удалён");
    }

    private Boolean checkEmail(String email) {
        return users.values().stream().anyMatch(user -> user.getEmail().equals(email));
    }
}
