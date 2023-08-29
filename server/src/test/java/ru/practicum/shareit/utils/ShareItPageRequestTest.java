package ru.practicum.shareit.utils;

import org.junit.Test;
import org.springframework.data.domain.Sort;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ShareItPageRequestTest {

    @Test
    public void testConstructor() {
        int from = 1;
        int size = 10;
        Sort sort = Sort.by("start").descending();

        ShareItPageRequest pageRequest = new ShareItPageRequest(from, size, sort);

        assertNotNull(pageRequest);
        assertEquals(size, pageRequest.getPageSize());
        assertEquals(sort, pageRequest.getSort());
    }
}
