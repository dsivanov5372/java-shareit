package ru.practicum.shareit.request.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Item {
    Long id;
    String name;
    String description;
    boolean available;
    Long owner;
    Long requestId;
    BookingInfo lastBooking;
    BookingInfo nextBooking;
    List<Comment> comments;
}