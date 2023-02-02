package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemRequestServiceTest {
    @Autowired
    private final ItemRequestService service;
    @MockBean
    private final UserRepository userRepository;
    @MockBean
    private final ItemRepository itemRepository;
    @MockBean
    private final ItemRequestRepository itemRequestRepository;

    private final User user = User.builder().id(1L).name("name").email("null@null.null").build();
    private final User user2 = User.builder().id(2L).name("name").email("null2@null.null").build();
    private final Item item = Item.builder().id(1L).name("test").description("test").owner(user.getId()).available(true).build();
    private final ItemRequest request = ItemRequest.builder().id(1L).requestorId(user2.getId()).description("text").build();

    @Test
    void shouldThrowExceptionIfUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        List<RuntimeException> exceptionList = new ArrayList<>();
        exceptionList.add(assertThrows(RuntimeException.class, () -> service.addRequest(3L, null)));
        exceptionList.add(assertThrows(RuntimeException.class, () -> service.getRequestById(3L, 2L)));
        exceptionList.add(assertThrows(RuntimeException.class, () -> service.getAllByUserId(3L)));
        exceptionList.add(assertThrows(RuntimeException.class, () -> service.getAllRequests(null, null, 3L)));
        exceptionList.forEach(ex -> assertEquals("User not found!", ex.getMessage()));
    }

    @Test
    void shouldReturnAllRequestsByRequestorId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(itemRequestRepository.findAllByRequestorId(anyLong())).thenReturn(List.of(request));
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(List.of(item));
        List<ItemRequest> requests = service.getAllByUserId(2L);
        assertEquals(1, requests.size());
        assertEquals(request, requests.get(0));
    }

    @Test
    void shouldReturnAllRequestsOfNotRequestor() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequestorIdIsNot(anyLong())).thenReturn(List.of(request));
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(List.of(item));
        List<ItemRequest> requests = service.getAllRequests(null, null, user.getId());
        assertEquals(1, requests.size());
        assertEquals(request, requests.get(0));
    }

    @Test
    void shouldFindRequestById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(List.of(item));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(request));
        assertEquals(request, service.getRequestById(user2.getId(), request.getId()));
    }

    @Test
    void shouldThrowExceptionIfRequestNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(List.of(item));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.getRequestById(2L, 2L));
        assertEquals("Request not found!", ex.getMessage());
    }

    @Test
    void shouldAddRequest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(itemRequestRepository.save(any())).thenReturn(request);
        ItemRequest result = service.addRequest(2L, ItemRequestDto.builder().description("test").userId(2L).build());
        assertEquals(result, request);
    }
}