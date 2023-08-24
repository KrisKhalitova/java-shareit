package ru.practicum.shareit.booking.dto;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.util.Optional;

@JsonTest
public class PostBookingDtoJsonTest {

    @Autowired
    private JacksonTester<PostBookingDto> jsonPostBookingDto;

    @Test
    @SneakyThrows
    void bookingDtoTest() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(1);
        PostBookingDto bookingDto = PostBookingDto.builder()
                .start(start)
                .end(end)
                .itemId(1L)
                .build();

        Optional<JsonContent<PostBookingDto>> result = Optional.of(jsonPostBookingDto.write(bookingDto));

        Assertions.assertThat(result)
                .isPresent()
                .hasValueSatisfying(i -> {
                    Assertions.assertThat(i)
                            .extractingJsonPathStringValue("$.start");
                    Assertions.assertThat(i)
                            .extractingJsonPathStringValue("$.end");
                });
    }
}
