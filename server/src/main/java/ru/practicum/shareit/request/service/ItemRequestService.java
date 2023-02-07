package ru.practicum.shareit.request.service;

import java.util.List;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

public interface ItemRequestService {
    List<ItemRequest> getAllByUserId(Long userId);

    List<ItemRequest> getAllRequests(Integer from, Integer size, Long userId);

    ItemRequest getRequestById(Long userId, Long requestId);

    ItemRequest addRequest(Long userId, ItemRequestDto request);
}