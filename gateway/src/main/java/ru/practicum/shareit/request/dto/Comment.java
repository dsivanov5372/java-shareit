package ru.practicum.shareit.request.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Comment {
    Long id;
    String text;
    String authorName;
    Long authorId;
    Long itemId;
    LocalDateTime created;
}