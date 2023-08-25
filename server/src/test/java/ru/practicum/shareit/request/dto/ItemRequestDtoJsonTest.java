package ru.practicum.shareit.request.dto;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.time.LocalDateTime;

@JsonTest
public class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ResponseItemRequestDto> json;

    @Test
    void itemRequestDtoTest() throws IOException {
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .id(1L)
                .name("User")
                .email("user@mail.ru")
                .build();
        ResponseItemRequestDto itemRequestDto = ResponseItemRequestDto.builder()
                .id(1L)
                .description("Item_description")
                .created(now)
                .requesterId(user.getId())
                .build();
        JsonContent<ResponseItemRequestDto> result = json.write(itemRequestDto);

        Assertions.assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        Assertions.assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemRequestDto.getDescription());
    }
}
