package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingInfo {
    private Long id;
    private Long bookerId;
}
