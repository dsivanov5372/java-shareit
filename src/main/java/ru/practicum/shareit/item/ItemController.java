package ru.practicum.shareit.item;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import ru.practicum.shareit.booking.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;


@RestController
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService service;
    private final String header = "X-Sharer-User-Id";

    @PostMapping
    public Item addItem(@RequestBody ItemDto item,
                        @RequestHeader(header) Long userId) {
        return service.addItem(item, userId);
    }

    @GetMapping
    public List<Item> findAllByUserId(@RequestParam(required = false) Integer from,
                                      @RequestParam(required = false) Integer size,
                                      @RequestHeader(header) Long userId) {
        return service.findAllByUserId(from, size, userId);
    }

    @GetMapping("/{itemId}")
    public Item findItemById(@RequestHeader(header) Long userId,
                             @PathVariable("itemId") Long itemId) {
        return service.findItemById(userId, itemId);
    }

    @GetMapping("/search")
    public List<Item> findAllByText(@RequestParam(required = false) Integer from,
                                    @RequestParam(required = false) Integer size,
                                    @RequestParam("text") String text) {
        return service.findAllByText(from, size, text);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestHeader(header) Long userId,
                           @PathVariable("itemId") Long itemId,
                           @RequestBody ItemDto item) {
        return service.updateItem(userId, itemId, item);
    }

    @PostMapping("/{itemId}/comment")
    public Comment addComment(@RequestHeader(header) Long userId,
                              @PathVariable("itemId") Long itemId,
                              @RequestBody CommentDto comment) {
        return service.addComment(userId, itemId, comment);
    }
}