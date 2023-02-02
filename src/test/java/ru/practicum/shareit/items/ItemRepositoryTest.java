package ru.practicum.shareit.items;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    private final User user = User.builder().name("null null").email("null@null.null").build();
    private final Item item = Item.builder().name("test item").description("test item").available(true).build();

    @Test
    void shouldFindItemWithTextInNameOrDescription() {
        userRepository.save(user);
        item.setOwner(user.getId());
        itemRepository.save(item);

        List<Item> items = itemRepository.searchItemByText("eSt");
        assertEquals(1, items.size());
        assertEquals(item, items.get(0));
    }

    @Test
    void shouldFindItemByItsOwnerId() {
        userRepository.save(user);
        item.setOwner(user.getId());
        itemRepository.save(item);

        List<Item> items = itemRepository.findByOwnerOrderById(item.getOwner());
        assertEquals(1, items.size());
        assertEquals(item, items.get(0));
    }

    @Test
    void shouldFindItemByRequestId() {
        userRepository.save(user);
        item.setOwner(user.getId());
        itemRepository.save(item);
        ItemRequest request = ItemRequest.builder()
                .created(LocalDateTime.now())
                .requestorId(1L)
                .items(List.of(item))
                .description("lol")
                .build();
        itemRequestRepository.save(request);
        item.setRequestId(request.getId());
        itemRepository.save(item);

        List<Item> items = itemRepository.findAllByRequestId(1L);
        assertEquals(1, items.size());
        assertEquals(item, items.get(0));
    }
}