package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingDtoWithBookers;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ResponseItemDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDtoWithBookers lastBooking;
    private BookingDtoWithBookers nextBooking;
    private UserDto owner;
    private List<ResponseCommentDto> comments;
    private Long requestId;
}
