package ru.practicum.shareitgateway.item.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ResponseItemListDto {
    @JsonValue
    private List<ResponseItemDto> items;
}
