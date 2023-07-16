package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {

    User createNewUser(User user);

    User updateUser(User user, long id);

    User getUserById(Long userId);

    Collection<User> getAllUsers();

    void deleteUser(Long userId);
}
