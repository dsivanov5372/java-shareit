package ru.practicum.shareit.requests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    private final User user = User.builder().name("null").email("null2@null.null").id(1L).build();
    private final User user2 = User.builder().name("null").email("null3@null.null").id(2L).build();
    private final Item item = Item.builder().name("item").description("item test").owner(1L).available(true).id(1L).build();
    private final ItemRequest request = ItemRequest.builder().requestorId(user2.getId()).description("text").build();

    @Test
    void shouldFindAllRequestsByRequestorId() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRequestRepository.save(request);
        item.setRequestId(request.getId());
        itemRepository.save(item);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorId(request.getRequestorId());
        assertEquals(1, requests.size());
        assertEquals(request, requests.get(0));
    }

    @Test
    void shouldFindAllRequestsNotFromRequestor() {
        userRepository.save(user);
        userRepository.save(user2);
        itemRequestRepository.save(request);
        item.setRequestId(request.getId());
        itemRepository.save(item);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdIsNot(user.getId(), PageRequest.of(0, 20));
        assertEquals(1, requests.size());
        assertEquals(request, requests.get(0));
    }
}