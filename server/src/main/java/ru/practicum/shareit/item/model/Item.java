package ru.practicum.shareit.item.model;

import java.util.List;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "items", schema = "public")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "description", nullable = false)
    String description;

    @Column(name = "is_available", nullable = false)
    boolean available;

    @Column(name = "owner_id", nullable = false)
    Long owner;

    @Column(name = "request_id", nullable = false)
    Long requestId;

    @Transient
    BookingInfo lastBooking;
    @Transient
    BookingInfo nextBooking;
    @Transient
    List<Comment> comments;
}