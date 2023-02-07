package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
public class ItemRequestDto {
    Long id;
    Long userId;
    @NotBlank
    String description;
    LocalDateTime created;
    @Builder.Default
    List<Item> items = new ArrayList<>();
}