package ru.practicum.shareitgateway.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareitgateway.item.dto.ItemDto;
import ru.practicum.shareitgateway.user.dto.UserDto;


import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ResponseBookingDto {

    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDto item;
    private UserDto booker;
    private BookingStatus status;
}
