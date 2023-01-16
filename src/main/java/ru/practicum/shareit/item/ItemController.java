package ru.practicum.shareit.item;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;


@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService service;
    private final String header = "X-Sharer-User-Id";

    @Autowired
    public ItemController(ItemService service) {
        this.service = service;
    }

    @PostMapping
    public Item addItem(@Valid @RequestBody ItemDto item,
                        @RequestHeader(value = header, required = true) Long userId) {
        return service.addItem(item, userId);
    }

    @GetMapping
    public List<Item> findAllByUser(@RequestHeader(value = header, required = true) Long userId) {
        return service.findAllByUser(userId);
    }

    @GetMapping("/{itemId}")
    public Item findItemById(@PathVariable("itemId") Long itemId) {
        return service.findItemById(itemId);
    }

    @GetMapping("/search")
    public List<Item> findAllByText(@RequestParam("text") String text) {
        return service.findAllByText(text);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestHeader(value = header, required = true) Long userId,
                           @PathVariable("itemId") Long itemId, @Valid @RequestBody ItemDto item) {
        return service.updateItem(userId, itemId, item);
    }
}
