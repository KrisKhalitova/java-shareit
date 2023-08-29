package ru.practicum.shareitgateway.item.dto;

import lombok.*;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ItemRequestDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}
