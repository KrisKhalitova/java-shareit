package ru.practicum.shareitgateway.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareitgateway.client.BaseClient;
import ru.practicum.shareitgateway.request.dto.PostItemRequestDto;

import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createNewItemRequest(Long requesterId, PostItemRequestDto itemRequestDto) {
        return post("", requesterId, itemRequestDto);
    }

    public ResponseEntity<Object> getAllOwnerRequests(Long requesterId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&&size={size}", requesterId, parameters);
    }

    public ResponseEntity<Object> getAllRequesterRequests(Long requesterId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/all?from={from}&&size={size}", requesterId, parameters);
    }

    public ResponseEntity<Object> getItemRequestById(Long userId, Long requestId) {
        return get("/" + requestId, userId);
    }
}
