package ru.practicum.shareit.item.repository;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT i FROM Item i " +
            "WHERE UPPER(i.name) LIKE UPPER(concat('%', :text, '%')) " +
            "OR UPPER(i.description) LIKE UPPER(concat('%', :text, '%')) " +
            "AND i.available = true")
    List<Item> searchItemByText(String text, PageRequest of);

    List<Item> findByOwnerOrderById(long userId, PageRequest of);

    List<Item> findAllByRequestId(long requestId);
}