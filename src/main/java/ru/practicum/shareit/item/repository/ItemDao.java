package ru.practicum.shareit.item.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;

public class ItemDao {
    private Long idSetter = 1L;
    private final Map<Long, Item> items = new HashMap<>();

    public Item addItem(ItemDto itemDto, Long userId) {
        itemDto.setId(idSetter++);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userId);

        items.put(item.getId(), item);
        return item;
    }

    public List<Item> findAllByUser(Long userId) {
        List<Item> result = new ArrayList<>();
        items.values().stream().forEach(item -> {
            if (item.getOwner() == userId) {
                result.add(item);
            }
        });

        return result;
    }

    public Item findItemById(Long itemId) {
        return items.values()
                    .stream()
                    .filter(item -> item.getId() == itemId)
                    .findFirst()
                    .get();
    }

    public List<Item> findAllByText(String text) {
        List<Item> result = new ArrayList<>();

        if (text != null && !text.isBlank()) {
            items.values().stream().forEach(item -> {
                if ((item.getName().toLowerCase().contains(text.toLowerCase()) ||
                    item.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                    item.getAvailable() == true) {
                    result.add(item);
                }
            });
        }

        return result;
    }

    public Item updateItem(ItemDto itemDto, Long userId) throws UserNotFoundException {
        Item toUpdate = items.values().stream().filter(obj -> obj.getId() == itemDto.getId()).findFirst().get();
        if (toUpdate == null) {
            return null;
        }

        if (toUpdate.getOwner() != userId) {
            throw new UserNotFoundException("User is not an owner");
        }
        if (itemDto.getName() != null) {
            toUpdate.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            toUpdate.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            toUpdate.setAvailable(itemDto.getAvailable());
        }

        return toUpdate;
    }
}
