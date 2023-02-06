package ru.practicum.shareit.request.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;

@Data
@Builder
@AllArgsConstructor
public class ItemRequestDto {
    Long id;
    Long userId;
    String description;
    LocalDateTime created;
    @Builder.Default
    List<Item> items = new ArrayList<>();
}