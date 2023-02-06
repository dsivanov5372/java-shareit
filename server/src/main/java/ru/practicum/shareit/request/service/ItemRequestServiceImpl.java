package ru.practicum.shareit.request.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;

    private void checkUserId(Long userId) throws UserNotFoundException {
        if (userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException("User not found!");
        }
    }

    @Override
    public List<ItemRequest> getAllByUserId(Long userId) {
        checkUserId(userId);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorId(userId);
        requests.forEach(request -> request.getItems().addAll(itemRepository.findAllByRequestId(request.getId())));
        return requests;
    }

    @Override
    public List<ItemRequest> getAllRequests(Integer from, Integer size, Long userId) {
        checkUserId(userId);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdIsNot(userId, PageRequest.of(from, size));
        requests.forEach(request -> request.getItems().addAll(itemRepository.findAllByRequestId(request.getId())));
        return requests;
    }

    @Override
    public ItemRequest getRequestById(Long userId, Long requestId) {
        checkUserId(userId);
        ItemRequest request = itemRequestRepository.findById(requestId)
                                                   .orElseThrow(() -> new RequestNotFoundException("Request not found!"));
        request.getItems().addAll(itemRepository.findAllByRequestId(requestId));
        return request;
    }

    @Override
    public ItemRequest addRequest(Long userId, ItemRequestDto request) {
        checkUserId(userId);
        ItemRequest toSave = ItemRequest.builder().description(request.getDescription()).requestorId(userId).build();
        return itemRequestRepository.save(toSave);
    }
}