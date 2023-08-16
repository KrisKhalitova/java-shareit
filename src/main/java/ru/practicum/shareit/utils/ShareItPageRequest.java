package ru.practicum.shareit.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class ShareItPageRequest extends PageRequest {

    public ShareItPageRequest(int from, int size, Sort sort) {
        super(from / size, size, sort);
    }
}
