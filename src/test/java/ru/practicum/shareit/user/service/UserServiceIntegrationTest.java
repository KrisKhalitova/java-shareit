package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(properties = {"db.name=test"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceIntegrationTest {

    private final UserService userService;
    private UserDto userOne;
    private UserDto userSecond;

    @BeforeEach
    void beforeEach() {
        userOne = UserDto.builder()
                .id(1L)
                .name("User_name")
                .email("usersone@test.testz")
                .build();

        userSecond = UserDto.builder()
                .id(2L)
                .name("User_name")
                .email("usersecond@test.testz")
                .build();
    }

    @Test
    public void getAllUsersTest() {
        User user1 = UserMapper.toUser(userOne);
        User user2 = UserMapper.toUser(userSecond);

        userService.createNewUser(userOne);
        user1.setId(1L);
        userService.createNewUser(userSecond);
        user2.setId(2L);

        List<UserDto> expectedUsers = List.of(userOne, userSecond);

        Collection<UserDto> actualUsers = userService.getAllUsers();

        assertEquals(expectedUsers.size(), actualUsers.size());
        assertEquals(2, actualUsers.size());
    }
}
