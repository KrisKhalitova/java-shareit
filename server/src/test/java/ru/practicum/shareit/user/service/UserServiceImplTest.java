package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    private UserDto userDto1;
    private UserDto userDto2;
    private User user1;
    private User user2;

    @BeforeEach
    void beforeEach() {
        user1 = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@mail.ru")
                .build();
        userDto1 = UserDto.builder()
                .id(user1.getId())
                .name(user1.getName())
                .email(user1.getEmail())
                .build();
        user2 = User.builder()
                .id(2L)
                .name("user2")
                .email("user2r@mail.ru")
                .build();
        userDto2 = UserDto.builder()
                .id(user2.getId())
                .name(user2.getName())
                .email(user2.getEmail())
                .build();
    }

    @Test
    void createNewUserTest() {
        when(userRepository.save(any())).thenReturn(user1);
        UserDto actualUser1 = userService.createNewUser(userDto1);

        assertNotNull(actualUser1);
        assertEquals(user1.getId(), actualUser1.getId());
        assertEquals(user1.getName(), actualUser1.getName());
        assertEquals(user1.getEmail(), actualUser1.getEmail());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void createNewUserWithEmptyNameTest() {
        user1.setName("");
        when(userRepository.save(any()))
                .thenThrow(new ValidationException("Невозможно создать пользователя с пустым именем."));

        assertThatThrownBy(() -> userService.createNewUser(userDto1)).isInstanceOf(ValidationException.class);
    }

    @Test
    void createNewUserWithEmptyEmailTest() {
        user1.setEmail("");
        when(userRepository.save(any()))
                .thenThrow(new ValidationException("Невозможно создать пользователя с пустым email."));

        assertThatThrownBy(() -> userService.createNewUser(userDto1)).isInstanceOf(ValidationException.class);
    }

    @Test
    void createNewUserWithWrongEmailTest() {
        user1.setEmail("email");
        when(userRepository.save(any()))
                .thenThrow(new ValidationException("Невозможно создать пользователя с неверным форматом email."));

        assertThatThrownBy(() -> userService.createNewUser(userDto1)).isInstanceOf(ValidationException.class);
    }

    @Test
    void updateUserTest() {
        Long userId = user1.getId();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));

        createNewUserTest();
        user1.setName("newNameUpdated");
        user2.setEmail("newEmailUpdated@test.ru");
        UserDto actualUser1 = userService.updateUser(UserMapper.toUserDto(user1), userId);

        assertNotNull(actualUser1);
        assertEquals(user1.getId(), actualUser1.getId());
        assertEquals(user1.getName(), actualUser1.getName());
        assertEquals(user1.getEmail(), actualUser1.getEmail());
    }

    @Test
    void updateUserWithEmptyNameTest() {
        user1.setName("");
        when(userRepository.save(any()))
                .thenThrow(new ValidationException("Невозможно обновить пользователя с пустым именем."));

        assertThatThrownBy(() -> userService.createNewUser(userDto1)).isInstanceOf(ValidationException.class);
    }

    @Test
    void updateUserWithNullNameTest() {
        user1.setName(null);
        when(userRepository.save(any()))
                .thenThrow(new ValidationException("Невозможно обновить пользователя с пустым именем."));

        assertThatThrownBy(() -> userService.createNewUser(userDto1)).isInstanceOf(ValidationException.class);
    }

    @Test
    void updateUserWithNullEmailTest() {
        user1.setEmail(null);
        when(userRepository.save(any()))
                .thenThrow(new ValidationException("Невозможно обновить пользователя с пустым email."));

        assertThatThrownBy(() -> userService.createNewUser(userDto1)).isInstanceOf(ValidationException.class);
    }

    @Test
    void updateUserWithEmptyEmailTest() {
        createNewUserTest();
        user1.setEmail("");
        when(userRepository.save(any()))
                .thenThrow(new ValidationException("Невозможно обновить пользователя с пустым email."));

        assertThatThrownBy(() -> userService.createNewUser(userDto1)).isInstanceOf(ValidationException.class);
    }

    @Test
    void updateUserWithWrongEmailTest() {
        createNewUserTest();
        user1.setEmail("wrong");
        when(userRepository.save(any()))
                .thenThrow(new ValidationException("Невозможно обновить пользователя с неверным форматом email."));

        assertThatThrownBy(() -> userService.createNewUser(userDto1)).isInstanceOf(ValidationException.class);
    }

    @Test
    void updateUserWithWrongIdTest() {
        createNewUserTest();

        assertThatThrownBy(() -> userService.updateUser(userDto2, 15L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void getUserByIdTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        UserDto userDtoGotten = userService.getUserById(user1.getId());

        assertNotNull(userDtoGotten);
        assertEquals(userDtoGotten, userDto1);
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void getUserByWrongIdTest() {
        when(userRepository.findById(anyLong()))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        assertThatThrownBy(() -> userService.getUserById(15L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    public void testGetUserById_InvalidUserId() {
        Long invalidUserId = -1L;
        when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            userService.getUserById(invalidUserId);
        });
    }

    @Test
    void getAllUsersTest() {
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        Collection<UserDto> usersDto = userService.getAllUsers();

        assertNotNull(usersDto);
        assertEquals(2, usersDto.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void deleteUserTest() {
        Long userId = 1L;
        User user = new User(userId, "userNew", "usernew@mail.ru");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void deleteUserWithWrongIdTest() {
        when(userRepository.findById(anyLong()))
                .thenThrow(new NotFoundException("Пользователь не найден."));

        assertThatThrownBy(() -> userService.deleteUser(200L)).isInstanceOf(NotFoundException.class);
    }
}