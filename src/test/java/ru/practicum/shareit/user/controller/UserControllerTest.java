package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;
    private UserDto userDto;

    @BeforeEach
    void beforeEach() {
        userDto = UserDto.builder()
                .id(1L)
                .name("username")
                .email("mail@test.ru")
                .build();
    }

    @Test
    void createNewUserTest() throws Exception {
        when(userService.createNewUser(any())).thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void createNewUserWithEmptyNameTest() throws Exception {
        userDto.setName("");
        when(userService.createNewUser(any()))
                .thenThrow(new ValidationException("Невозможно создать пользователя с пустым именем."));

        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createNewUserWithWrongEmailTest() throws Exception {
        userDto.setEmail("Email");
        when(userService.createNewUser(any()))
                .thenThrow(new ValidationException("Email имеет неверный формат."));

        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createNewUserWithEmptyEmailTest() throws Exception {
        userDto.setEmail("");
        when(userService.createNewUser(any()))
                .thenThrow(new ValidationException("Невозможно создать пользователя с пустым email."));

        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUserTest() throws Exception {
        userDto.setName("newNameUpdated");
        userDto.setEmail("newEmailUpdated@test.ru");
        when(userService.updateUser(any(), anyLong())).thenReturn(userDto);

        mvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void updateUserWithEmptyNameTest() throws Exception {
        userDto.setName("");
        when(userService.updateUser(any(), anyLong()))
                .thenThrow(new ValidationException("Невозможно обновить пользователя задав пустое имя."));

        mvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUserWithEmptyEmailTest() throws Exception {
        userDto.setEmail("");
        when(userService.updateUser(any(), anyLong()))
                .thenThrow(new ValidationException("Невозможно обновить пользователя задав пустой email."));

        mvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUserWithWrongEmailTest() throws Exception {
        userDto.setName("wrongemail");
        when(userService.updateUser(any(), anyLong()))
                .thenThrow(new ValidationException("Невозможно обновить пользователя задав неверный формат email."));

        mvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserByIdTest() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(userDto);

        mvc.perform(get("/users/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void getUserByWrongIdTest() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenThrow(new NotFoundException("Пользователь не найден."));

        mvc.perform(get("/users/{userId}", 2L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsersTest() throws Exception {
        List<UserDto> users = new ArrayList<>();
        users.add(userDto);
        when(userService.getAllUsers()).thenReturn(users);

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail())));
    }

    @Test
    void deleteUserTest() throws Exception {
        Long userId = 1L;
        mvc.perform(delete("/users/{id}", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).deleteUser(userId);
    }
}