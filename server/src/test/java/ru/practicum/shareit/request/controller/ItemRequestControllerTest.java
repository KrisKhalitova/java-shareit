package ru.practicum.shareit.request.controller;

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
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestListDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemRequestService itemRequestService;
    private ResponseItemRequestDto responseItemRequestDto;
    private final LocalDateTime createdDate = LocalDateTime.now().plusDays(1);
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @BeforeEach
    void beforeEach() {
        responseItemRequestDto = ResponseItemRequestDto.builder()
                .id(1L)
                .created(createdDate)
                .requesterId(1L)
                .description("Описание заявки")
                .build();
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .name("Название заявки")
                .description("Описание заявки")
                .available(Boolean.TRUE)
                .requestId(1L)
                .build();
    }

    @Test
    void createNewItemRequestTest() throws Exception {
        when(itemRequestService.createNewItemRequest(any(), anyLong())).thenReturn(responseItemRequestDto);

        mvc.perform(post("/requests")
                        .header(USER_ID_HEADER, 1)
                        .content(objectMapper.writeValueAsString(responseItemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void createNewItemRequestWithEmptyDescriptionTest() throws Exception {
        responseItemRequestDto.setDescription("");
        when(itemRequestService.createNewItemRequest(any(), anyLong()))
                .thenThrow(new ValidationException("Невозможно создать запрос без описания."));

        mvc.perform(post("/requests")
                        .header(USER_ID_HEADER, 1)
                        .content(objectMapper.writeValueAsString(responseItemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createNewItemRequestWithWrongUserIdTest() throws Exception {
        when(itemRequestService.createNewItemRequest(any(), anyLong()))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mvc.perform(post("/requests")
                        .header(USER_ID_HEADER, 256)
                        .content(objectMapper.writeValueAsString(responseItemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllOwnerRequestsTest() throws Exception {
        List<ResponseItemRequestDto> itemRequestsDto = new ArrayList<>();
        itemRequestsDto.add(responseItemRequestDto);

        ResponseItemRequestListDto itemRequests = ResponseItemRequestListDto.builder()
                .requests(itemRequestsDto).build();
        when(itemRequestService.getAllOwnerRequests(anyInt(), anyInt(), anyLong())).thenReturn(itemRequests);

        mvc.perform(get("/requests")
                        .header(USER_ID_HEADER, 1L)
                        .param("from", "0")
                        .param("size", "20")
                        .content(objectMapper.writeValueAsString(responseItemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

    }

    @Test
    void getAllRequesterRequestsTest() throws Exception {
        List<ResponseItemRequestDto> itemRequestsDto = new ArrayList<>();
        itemRequestsDto.add(responseItemRequestDto);

        ResponseItemRequestListDto itemRequests = ResponseItemRequestListDto.builder()
                .requests(itemRequestsDto).build();

        when(itemRequestService.getAllRequesterRequests(anyInt(), anyInt(), anyLong())).thenReturn(itemRequests);

        mvc.perform(get("/requests/all")
                        .header(USER_ID_HEADER, 1L)
                        .param("from", "0")
                        .param("size", "20")
                        .content(objectMapper.writeValueAsString(responseItemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void getItemRequestByIdTest() throws Exception {
        when(itemRequestService.getItemRequestById(anyLong(), anyLong())).thenReturn(responseItemRequestDto);

        mvc.perform(get("/requests/{requestId}", 1L)
                        .header(USER_ID_HEADER, 1L)
                        .content(objectMapper.writeValueAsString(responseItemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void getItemRequestByWrongRequestIdTest() throws Exception {
        when(itemRequestService.getItemRequestById(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("Запрос не найден"));

        mvc.perform(get("/requests/{requestId}", 900L)
                        .header(USER_ID_HEADER, 1L)
                        .content(objectMapper.writeValueAsString(responseItemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getItemRequestByWrongUserIdTest() throws Exception {
        when(itemRequestService.getItemRequestById(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mvc.perform(get("/requests/{requestId}", 1L)
                        .header(USER_ID_HEADER, 256)
                        .content(objectMapper.writeValueAsString(responseItemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}