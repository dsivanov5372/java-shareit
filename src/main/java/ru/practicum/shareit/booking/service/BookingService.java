package ru.practicum.shareit.booking.service;

import java.util.List;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;

public interface BookingService {
    Booking addBooking(Long userId, BookingDto booking);

    Booking updateBooking(Long userId, Boolean isApproved, Long bookingId);

    Booking getBookingById(Long userId, Long bookingId);

    List<Booking> getAllByUserId(Long userId, State state);

    List<Booking> getAllByOwnerId(Long userId, State state);
}
