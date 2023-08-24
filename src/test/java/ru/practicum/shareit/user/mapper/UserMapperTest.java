package ru.practicum.shareit.user.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UserMapperTest {

    private User user;
    private UserDto userDto;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "username", "user@mail.ru");
        userDto = new UserDto(2L, "UserDtoname", "userdto@mail.ru");
    }

    @Test
    void userDtoToUserTest() {
        User user = UserMapper.toUser(userDto);

        assertThat(user.getName()).isEqualTo(userDto.getName());
        assertThat(user.getEmail()).isEqualTo(userDto.getEmail());
    }

    @Test
    void userToUserDtoTest() {
        UserDto userDto = UserMapper.toUserDto(user);

        assertThat(userDto.getName()).isEqualTo(user.getName());
        assertThat(userDto.getEmail()).isEqualTo(user.getEmail());
    }
}