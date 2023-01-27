package ru.practicum.shareit.item.service;

import java.util.List;

import ru.practicum.shareit.booking.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

public interface ItemService {
    Item addItem(ItemDto item, Long userId);

    List<Item> findAllByUserId(Long userId);

    Item findItemById(Long userId, Long itemId);

    List<Item> findAllByText(String text);

    Item updateItem(Long userId, Long itemId, ItemDto item);

    Comment addComment(Long userId, Long itemId, CommentDto comment);
}
