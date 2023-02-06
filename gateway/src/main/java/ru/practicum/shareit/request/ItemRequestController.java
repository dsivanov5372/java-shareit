package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.exception.PageSizeException;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/requests")
public class ItemRequestController {
    private final String header = "X-Sharer-User-Id";
    private final ItemRequestClient itemRequestClient;

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader(header) Long userId,
                                    @RequestParam(value = "from", defaultValue = "0") Integer from,
                                    @RequestParam(value = "size", defaultValue = "10") Integer size) {
        checkParams(from, size);
        return itemRequestClient.getAll(userId, from, size);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestsByUser(@RequestHeader(header) Long userId) {
        return itemRequestClient.getItemRequestsByUser(userId);
    }

    @GetMapping(value = "/{requestId}")
    public ResponseEntity<Object> getItemRequest(@RequestHeader(header) Long userId,
                                                 @PathVariable Long requestId) {
        return itemRequestClient.getItemRequest(requestId, userId);
    }

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(header) Long userId,
                                                    @Valid @RequestBody ItemRequestDto requestDto) {
        return itemRequestClient.createItemRequest(userId, requestDto);
    }

    private void checkParams(Integer from, Integer size) {
        if (from < 0 || size < 1) {
            throw new PageSizeException("Invalid pagination parameters!");
        }
    }
}