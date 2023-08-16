package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private BookingService bookingService;
    private final LocalDateTime start = LocalDateTime.now().plusDays(1);
    private final LocalDateTime end = start.plusDays(2);
    private ResponseBookingDto responseBookingDto;
    private ItemDto itemDto;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @BeforeEach
    void beforeEach() {
        itemDto = ItemDto.builder()
                .id(1L)
                .name("item")
                .description("item description")
                .available(Boolean.TRUE)
                .build();
        responseBookingDto = ResponseBookingDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .status(BookingStatus.WAITING)
                .item(itemDto)
                .build();
    }

    @Test
    void addNewRequestForBookingTest() throws Exception {
        when(bookingService.addNewRequestForBooking(any(), anyLong())).thenReturn(responseBookingDto);

        mvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, 1)
                        .content(objectMapper.writeValueAsString(responseBookingDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void addNewRequestForBookingTestWithWrongStartTest() throws Exception {
        responseBookingDto.setStart(LocalDateTime.now().minusDays(365));
        when(bookingService.addNewRequestForBooking(any(), anyLong())).
                thenThrow(new ValidationException("Дата начала не может быть установлена в прошлом"));

        mvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, 1)
                        .content(objectMapper.writeValueAsString(responseBookingDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addNewRequestForBookingTestWithWrongEndTest() throws Exception {
        responseBookingDto.setEnd(LocalDateTime.now().minusDays(365));
        when(bookingService.addNewRequestForBooking(any(), anyLong())).
                thenThrow(new ValidationException("Дата окончания не может быть установлена в прошлом"));

        mvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, 1)
                        .content(objectMapper.writeValueAsString(responseBookingDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addNewRequestForBookingTestWithWrongUserIdTest() throws Exception {
        when(bookingService.addNewRequestForBooking(any(), anyLong())).
                thenThrow(new NotFoundException("Пользователь не найден"));

        mvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, 256)
                        .content(objectMapper.writeValueAsString(responseBookingDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void approveBookingTest() throws Exception {
        when(bookingService.approveBooking(anyLong(), anyBoolean(), anyLong())).thenReturn(responseBookingDto);

        String result = mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(USER_ID_HEADER, 1)
                        .content(objectMapper.writeValueAsString(responseBookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("bookingId", "1L")
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(responseBookingDto), result);
    }

    @Test
    void getBookingByBookingIdTest() throws Exception {
        when(bookingService.getBookingByBookingId(anyLong(), anyLong())).thenReturn(responseBookingDto);

        String result = mvc.perform(get("/bookings/{bookingId}", 2L)
                        .header(USER_ID_HEADER, 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(responseBookingDto), result);
    }

    @Test
    void getBookingByBookingIdWithWrongUserIdTest() throws Exception {
        when(bookingService.getBookingByBookingId(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mvc.perform(get("/bookings/{bookingId}", 2L)
                        .header(USER_ID_HEADER, 256)
                        .content(objectMapper.writeValueAsString(responseBookingDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllBookingsByUserIdByStateTest() throws Exception {
        List<ResponseBookingDto> bookings = new ArrayList<>();
        bookings.add(responseBookingDto);
        when(bookingService.getAllBookingsByUserIdByState(any(), anyLong(), anyInt(), anyInt()))
                .thenReturn(bookings);

        mvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, 1)
                        .param("state", "ALL")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(20)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void getAllBookingsByOwnerByStateTest() throws Exception {
        List<ResponseBookingDto> bookings = new ArrayList<>();
        bookings.add(responseBookingDto);
        when(bookingService.getAllBookingsByOwnerByState(any(), anyLong(), anyInt(),
                anyInt())).thenReturn(List.of(responseBookingDto));
        mvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, 1)
                        .param("state", "ALL")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(20)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }
}