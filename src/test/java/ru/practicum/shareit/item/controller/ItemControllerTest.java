package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseCommentDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.dto.ResponseItemListDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;
    private ItemDto itemDto;
    private ResponseItemDto responseItemDto;
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private ResponseCommentDto responseCommentDto;
    private ResponseItemListDto responseItemListDto;

    @BeforeEach
    void beforeEach() {
        itemDto = ItemDto.builder()
                .id(1L)
                .name("item")
                .description("item description")
                .available(Boolean.TRUE)
                .build();
        responseItemDto = ResponseItemDto.builder()
                .id(2L)
                .name("responseName")
                .description("responseDescription")
                .available(Boolean.TRUE)
                .build();
        responseCommentDto = ResponseCommentDto.builder()
                .id(1L)
                .text("comment text")
                .authorName("AuthorName")
                .build();
    }

    @Test
    void addNewItemTest() throws Exception {
        when(itemService.addNewItem(any(), anyLong())).thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header(USER_ID_HEADER, 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId())));
    }

    @Test
    void addNewItemWithEmptyNameTest() throws Exception {
        itemDto.setName("");
        when(itemService.addNewItem(any(), anyLong()))
                .thenThrow(new ValidationException("Невозможно создать вещь с пустым названием."));

        mvc.perform(post("/items")
                        .header(USER_ID_HEADER, 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addNewItemWithEmptyDescriptionTest() throws Exception {
        itemDto.setDescription("");
        when(itemService.addNewItem(any(), anyLong()))
                .thenThrow(new ValidationException("Невозможно создать вещь с пустым описанием."));

        mvc.perform(post("/items")
                        .header(USER_ID_HEADER, 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addNewItemWithEmptyAvailableTest() throws Exception {
        itemDto.setAvailable(null);
        when(itemService.addNewItem(any(), anyLong()))
                .thenThrow(new ValidationException("Невозможно создать вещь с пустым статусом доступности."));

        mvc.perform(post("/items")
                        .header(USER_ID_HEADER, 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addNewItemWithWrongUserTest() throws Exception {
        when(itemService.addNewItem(any(), anyLong()))
                .thenThrow(new NotFoundException("Пользователь не существует"));

        mvc.perform(post("/items")
                        .header(USER_ID_HEADER, 256)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateItemByIdTest() throws Exception {
        itemDto.setName("newItemNameUpdated");
        itemDto.setDescription("newDescriptionUpdated");
        itemDto.setAvailable(Boolean.FALSE);

        when(itemService.updateItemById(any(), anyLong(), anyLong())).thenReturn(itemDto);
        mvc.perform(patch("/items/{itemId}", 1L)
                        .header(USER_ID_HEADER, 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId())));
    }

    @Test
    void updateItemById() throws Exception {
        itemDto.setName("newItemNameUpdated");
        itemDto.setDescription("newDescriptionUpdated");
        itemDto.setAvailable(Boolean.FALSE);

        when(itemService.updateItemById(any(), anyLong(), anyLong())).thenReturn(itemDto);
        mvc.perform(patch("/items/{itemId}", 1L)
                        .header(USER_ID_HEADER, 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId())));
    }

    @Test
    void updateItemWithEmptyNameTest() throws Exception {
        itemDto.setName("");
        when(itemService.updateItemById(any(), anyLong(), anyLong()))
                .thenThrow(new ValidationException("Невозможно обновить информацию о вещи задав пустое название."));

        mvc.perform(patch("/items/{itemId}", 1L)
                        .header(USER_ID_HEADER, 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItemWithEmptyDescriptionTest() throws Exception {
        itemDto.setDescription("");
        when(itemService.updateItemById(any(), anyLong(), anyLong()))
                .thenThrow(new ValidationException("Невозможно обновить информацию о вещи задав пустое описание."));

        mvc.perform(patch("/items/{itemId}", 1L)
                        .header(USER_ID_HEADER, 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItemWithEmptyAvailableTest() throws Exception {
        itemDto.setAvailable(null);
        when(itemService.updateItemById(any(), anyLong(), anyLong()))
                .thenThrow(new ValidationException("Невозможно обновить информацию о вещи задав пустой статус доступности."));

        mvc.perform(patch("/items/{itemId}", 1L)
                        .header(USER_ID_HEADER, 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItemWithWrongUserIdTest() throws Exception {
        when(itemService.updateItemById(any(), anyLong(), anyLong()))
                .thenThrow(new ValidationException("Невозможно обновить информацию по неверному id пользователя."));

        mvc.perform(patch("/items/{itemId}", 1L)
                        .header(USER_ID_HEADER, 256)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItemByIdTest() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(responseItemDto);

        mvc.perform(get("/items/{itemId}", 2L)
                        .header(USER_ID_HEADER, 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(responseItemDto.getName())))
                .andExpect(jsonPath("$.description", is(responseItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(responseItemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(responseItemDto.getRequestId())));
    }

    @Test
    void getItemByWrongIdTest() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("Вещь не найдена."));

        mvc.perform(get("/items/{itemId}", 5L)
                        .header(USER_ID_HEADER, 1)
                        .content(objectMapper.writeValueAsString(responseItemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getItemByWrongUserIdTest() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("Пользователь не найден."));

        mvc.perform(get("/items/{itemId}", 1L)
                        .header(USER_ID_HEADER, 256)
                        .content(objectMapper.writeValueAsString(responseItemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllItemsByUserIdTest() throws Exception {
        when(itemService.getAllItemsByUserId(anyLong(), anyInt(), anyInt())).thenReturn(responseItemListDto);

        mvc.perform(get("/items")
                        .header(USER_ID_HEADER, 1)
                        .content(objectMapper.writeValueAsString(responseItemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAllItemsByWrongUserIdTest() throws Exception {
        when(itemService.getAllItemsByUserId(anyLong(), anyInt(), anyInt()))
                .thenThrow(new NotFoundException("Пользователь не найден."));

        mvc.perform(get("/items")
                        .header(USER_ID_HEADER, 256)
                        .content(objectMapper.writeValueAsString(responseItemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchItemByTextTest() throws Exception {
        when(itemService.searchItemByText(anyString(), anyInt(), anyInt())).thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search")
                        .header(USER_ID_HEADER, 1L)
                        .param("text", "name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void addNewCommentTest() throws Exception {
        when(itemService.addNewComment(any(), anyLong(), anyLong())).thenReturn(responseCommentDto);

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .header(USER_ID_HEADER, 1L)
                        .content(objectMapper.writeValueAsString(responseCommentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseCommentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(responseCommentDto.getText())))
                .andExpect(jsonPath("$.created", is(responseCommentDto.getCreated())))
                .andExpect(jsonPath("$.authorName", is(responseCommentDto.getAuthorName())));
    }

    @Test
    void addNewCommentWithEmptyText() throws Exception {
        responseCommentDto.setText("");
        when(itemService.addNewComment(any(), anyLong(), anyLong()))
                .thenThrow(new ValidationException("Невозможно добавить отзыв с пустым текстом."));

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .header(USER_ID_HEADER, 1L)
                        .content(objectMapper.writeValueAsString(responseCommentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addNewCommentWithWrongUserId() throws Exception {
        when(itemService.addNewComment(any(), anyLong(), anyLong()))
                .thenThrow(new NotFoundException("Пользователь не найден."));

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .header(USER_ID_HEADER, 256L)
                        .content(objectMapper.writeValueAsString(responseCommentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void addNewCommentWithWrongItemId() throws Exception {
        when(itemService.addNewComment(any(), anyLong(), anyLong()))
                .thenThrow(new NotFoundException("Вещь не найдена."));

        mvc.perform(post("/items/{itemId}/comment", 800L)
                        .header(USER_ID_HEADER, 1L)
                        .content(objectMapper.writeValueAsString(responseCommentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}