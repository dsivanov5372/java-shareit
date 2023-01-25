package ru.practicum.shareit.booking;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.InvalidStateException;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final String header = "X-Sharer-User-Id";
    private final BookingService service;

    @PostMapping
    public Booking addBooking(@RequestHeader(header) Long userId,
                              @RequestBody BookingDto booking) {
        return service.addBooking(userId, booking);
    }

    @PatchMapping("/{bookingId}")
    public Booking updateBooking(@RequestHeader(header) Long userId,
                                 @RequestParam("approved") Boolean isApproved,
                                 @PathVariable("bookingId") Long bookingId) {
        return service.updateBooking(userId, isApproved, bookingId);
    }

    @GetMapping("/{bookingId}")
    public Booking getBookingById(@RequestHeader(header) Long userId,
                                  @PathVariable("bookingId") Long bookingId) {
        return service.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<Booking> getAllByUserId(@RequestHeader(header) Long userId,
                                        @RequestParam(value = "state", defaultValue = "ALL") String state) {
        checkState(state);
        return service.getAllByUserId(userId, State.valueOf(state));
    }

    @GetMapping("/owner")
    public List<Booking> getAllByOwnerId(@RequestHeader(header) Long userId,
                                         @RequestParam(value = "state", defaultValue = "ALL") String state) {
        checkState(state);
        return service.getAllByOwnerId(userId, State.valueOf(state));
    }

    private void checkState(String state) {
        if (!State.ALL.toString().equals(state) &&
            !State.PAST.toString().equals(state) &&
            !State.CURRENT.toString().equals(state) &&
            !State.FUTURE.toString().equals(state) &&
            !State.WAITING.toString().equals(state) &&
            !State.REJECTED.toString().equals(state)) {
            throw new InvalidStateException("Unknown state: " + state);
        }
    }
}