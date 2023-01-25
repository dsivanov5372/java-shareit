package ru.practicum.shareit.booking.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;

    @Override
    public Booking addBooking(Long userId, BookingDto booking) {
        Item item = itemRepository.findById(booking.getItemId())
                                  .orElseThrow(() -> new ItemNotFoundException("Item not found!"));
        User owner = userService.getUser(item.getOwner());
        User booker = userService.getUser(userId);

        if (owner.getId().equals(booker.getId())) {
            throw new UserNotFoundException("Owner can not book his item!");
        }

        if (!item.isAvailable()) {
            throw new BookingException("Item is not available!");
        }

        if (booking.getEnd().isBefore(booking.getStart()) ||
            booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new BookingException("Invalid end time of booking!");
        }

        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new BookingException("Invalid start time of booking!");
        }

        Booking toSave = Booking.builder()
                                .start(booking.getStart())
                                .end(booking.getEnd())
                                .item(item)
                                .booker(booker)
                                .status(Status.WAITING)
                                .build();

        toSave = bookingRepository.save(toSave);
        return toSave;
    }

    @Override
    public Booking updateBooking(Long userId, Boolean isApproved, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingException("Booking not found!"));

        if (!booking.getItem().getOwner().equals(userId)) {
            throw new UserNotFoundException("User is not an owner!");
        }

        if (booking.getStatus().equals(Status.REJECTED) ||
            booking.getStatus().equals(Status.APPROVED)) {
            throw new BookingException("booking has been already approved/rejected");
        }

        if (isApproved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        booking = bookingRepository.save(booking);

        return booking;
    }

    @Override
    public Booking getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                                           .orElseThrow(() -> new BookingNotFoundException("Booking not found!"));

        if (!booking.getBooker().getId().equals(userId) &&
            !booking.getItem().getOwner().equals(userId)) {
            throw new UserNotFoundException("User is not an owner!");
        }

        return booking;
    }

    @Override
    public List<Booking> getAllByUserId(Long userId, State state) {
        if (userService.getUser(userId) == null) {
            throw new UserNotFoundException("User not found!");
        }

        List<Booking> bookings = bookingRepository.findBookingsByUserId(userId);
        if (bookings.isEmpty()) {
            throw new BookingException("User does not have bookings!");
        }

        return filterBookingsByState(bookings, state);
    }

    @Override
    public List<Booking> getAllByOwnerId(Long userId, State state) {
        if (userService.getUser(userId) == null) {
            throw new UserNotFoundException("User not found!");
        }

        List<Booking> bookings = bookingRepository.findBookingsByOwnerId(userId);
        if (bookings.isEmpty()) {
            throw new BookingException("User does not have bookings!");
        }

        return filterBookingsByState(bookings, state);
    }

    private List<Booking> filterBookingsByState(List<Booking> bookings, State state) {
        switch (state) {
            case CURRENT:
                return bookings.stream().filter(booking -> booking.getStart().isBefore(LocalDateTime.now()) &&
                        booking.getEnd().isAfter(LocalDateTime.now())).collect(Collectors.toList());
            case PAST:
                return bookings.stream().filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                               .collect(Collectors.toList());
            case FUTURE:
                return bookings.stream().filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                               .collect(Collectors.toList());
            case WAITING:
                return bookings.stream().filter(booking -> booking.getStatus().equals(Status.WAITING))
                               .collect(Collectors.toList());
            case REJECTED:
                return bookings.stream().filter(booking -> booking.getStatus().equals(Status.REJECTED))
                               .collect(Collectors.toList());
            default:
        }

        return bookings;
    }
}