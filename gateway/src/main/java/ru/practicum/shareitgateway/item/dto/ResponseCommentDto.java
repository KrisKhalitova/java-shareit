package ru.practicum.shareitgateway.item.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
public class ResponseCommentDto {

    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
