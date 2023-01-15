package ru.practicum.shareit.item.model;

import ru.practicum.shareit.item.dto.ItemDto;

public class ItemMapper {
    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                   .id(itemDto.getId())
                   .available(itemDto.getAvailable())
                   .name(itemDto.getName())
                   .description(itemDto.getDescription())
                   .build();
    }
}
