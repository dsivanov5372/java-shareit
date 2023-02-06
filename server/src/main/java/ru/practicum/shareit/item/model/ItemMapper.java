package ru.practicum.shareit.item.model;

import ru.practicum.shareit.item.dto.ItemDto;

public class ItemMapper {
    public static Item toItem(ItemDto itemDto, Long userId) {
        return Item.builder()
                   .id(itemDto.getId())
                   .available(itemDto.getAvailable())
                   .description(itemDto.getDescription())
                   .name(itemDto.getName())
                   .owner(userId)
                   .requestId(itemDto.getRequestId())
                   .build();
    }
}
