package ru.practicum.shareitgateway.request.dto;

import lombok.*;
import ru.practicum.shareitgateway.item.dto.ItemRequestDto;


import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ResponseItemRequestDto {

    private Long id;
    private String description;
    private List<ItemRequestDto> items;
    private LocalDateTime created;
    private Long requesterId;
}
