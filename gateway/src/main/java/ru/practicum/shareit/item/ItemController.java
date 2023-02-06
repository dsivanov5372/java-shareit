package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.exception.PageSizeException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;

@Controller
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final String header = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getItemsByOwner(@RequestHeader(header) Long ownerId,
                        @RequestParam(value = "from", defaultValue = "0") Integer from,
                        @RequestParam(value = "size", defaultValue = "20") Integer size) {
        checkParams(from, size);
        return itemClient.getItemsByOwner(ownerId, from, size);
    }


    @GetMapping("/search")
    public ResponseEntity<Object> getItemsByText(@RequestParam String text,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        checkParams(from, size);
        return itemClient.getItemsByText(text, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(header) Long userId,
                                          @PathVariable Long itemId) {
        return itemClient.getItem(itemId, userId);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(header) Long userId,
                                             @RequestBody ItemDto itemDto) {
        return itemClient.createItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(header) Long userId,
                                                @PathVariable Long itemId,
                                                @Valid @RequestBody CommentDto commentDto) {
        return itemClient.createComment(itemId, userId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(header) Long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody ItemDto itemDto) {
        return itemClient.updateItem(itemDto, itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@RequestHeader(header) Long userId,
                                             @PathVariable Long itemId) {
        return itemClient.deleteItem(itemId, userId);
    }

    private void checkParams(Integer from, Integer size) {
        if (from < 0 || size < 1) {
            throw new PageSizeException("Invalid pagination parameters!");
        }
    }
}