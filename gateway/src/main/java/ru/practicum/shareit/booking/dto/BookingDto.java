package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingDto {
    Long itemId;
    LocalDateTime start;
    LocalDateTime end;
}
