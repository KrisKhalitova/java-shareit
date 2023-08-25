package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PostBookingDto {

    private Long id;
    private Long itemId;
    @FutureOrPresent(message = "Дата начала не может быть установлена в прошлом")
    @NotNull
    private LocalDateTime start;
    @Future(message = "Дата завершения не может быть в прошлом")
    @NotNull
    private LocalDateTime end;
}
