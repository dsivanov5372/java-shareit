package ru.practicum.shareit.request;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;



@AllArgsConstructor
@RestController
@RequestMapping("/requests")
public class ItemRequestController {
    private final String header = "X-Sharer-User-Id";
    public final ItemRequestService service;

    @GetMapping
    public List<ItemRequest> getAllByUserId(@RequestHeader(header) Long userId) {
        return service.getAllByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequest> getAllRequests(@RequestParam Integer from,
                                            @RequestParam Integer size,
                                            @RequestHeader(header) Long userId) {
        return service.getAllRequests(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequest getRequestById(@RequestHeader(header) Long userId,
                                      @PathVariable Long requestId) {
        return service.getRequestById(userId, requestId);
    }

    @PostMapping
    public ItemRequest addRequest(@RequestHeader(header) Long userId,
                                  @RequestBody ItemRequestDto request) {
        return service.addRequest(userId, request);
    }
}