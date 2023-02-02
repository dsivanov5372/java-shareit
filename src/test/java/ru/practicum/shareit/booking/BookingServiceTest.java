package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BookingServiceTest {
    @Autowired
    private BookingService bookingService;
    @MockBean
    private BookingRepository mockBookingRepository;
    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private UserService mockUserService;
    private Booking booking;
    private BookingDto bookingDto;
    private final User user = User.builder()
                                  .id(1L)
                                  .email("test@test.test")
                                  .name("test")
                                  .build();
    private final Item item = Item.builder()
                                  .id(1L)
                                  .name("test")
                                  .description("test")
                                  .owner(user.getId())
                                  .available(true)
                                  .build();

    @BeforeEach
    void setUp() {
        LocalDateTime time = LocalDateTime.now();
        booking = Booking.builder()
                         .id(1L)
                         .booker(User.builder().id(2L).build())
                         .item(item)
                         .start(time.plusDays(1))
                         .end(time.plusDays(2))
                         .status(Status.WAITING)
                         .build();
        bookingDto = BookingDto.builder()
                               .itemId(item.getId())
                               .start(booking.getStart())
                               .end(booking.getEnd())
                               .build();
    }

    @Test
    void shouldAddBooking() {
        when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(item));
        when(mockUserService.getUser(2L)).thenReturn(User.builder().id(2L).build());
        when(mockUserService.getUser(1L)).thenReturn(user);
        when(mockBookingRepository.save(any())).thenReturn(booking);

        Booking booking = bookingService.addBooking(2L, bookingDto);
        assertEquals(1L, booking.getId());
        assertEquals(2L, booking.getBooker().getId());
        assertEquals(item, booking.getItem());
        assertEquals(Status.WAITING, booking.getStatus());
    }

    @Test
    void shouldThrowExceptionIfBookerIsOwner() {
        when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(item));
        when(mockUserService.getUser(2L)).thenReturn(User.builder().id(2L).build());
        when(mockUserService.getUser(1L)).thenReturn(user);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> bookingService.addBooking(1L, bookingDto));
        assertEquals("Owner can not book his item!", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionIfItemIsNotAvailable() {
        when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(item));
        when(mockUserService.getUser(2L)).thenReturn(User.builder().id(2L).build());
        when(mockUserService.getUser(1L)).thenReturn(user);
        Objects.requireNonNull(item).setAvailable(false);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> bookingService.addBooking(2L, bookingDto));
        assertEquals("Item is not available!", ex.getMessage());
        Objects.requireNonNull(item).setAvailable(true);
    }

    @Test
    void shouldThrowExceptionIfInvalidEndTime() {
        when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(item));
        when(mockUserService.getUser(2L)).thenReturn(User.builder().id(2L).build());
        when(mockUserService.getUser(1L)).thenReturn(user);
        bookingDto.setEnd(bookingDto.getStart().minusMinutes(1));

        RuntimeException ex1 = assertThrows(RuntimeException.class, () -> bookingService.addBooking(2L, bookingDto));
        assertEquals("Invalid end time of booking!", ex1.getMessage());

        bookingDto.setEnd(LocalDateTime.now().minusDays(1));
        RuntimeException ex2 = assertThrows(RuntimeException.class, () -> bookingService.addBooking(2L, bookingDto));
        assertEquals("Invalid end time of booking!", ex2.getMessage());
    }

    @Test
    void shouldThrowExceptionIfInvalidStartTime() {
        when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(item));
        when(mockUserService.getUser(2L)).thenReturn(User.builder().id(2L).build());
        when(mockUserService.getUser(1L)).thenReturn(user);
        bookingDto.setStart(LocalDateTime.now().minusDays(1));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> bookingService.addBooking(2L, bookingDto));
        assertEquals("Invalid start time of booking!", ex.getMessage());
    }

    @Test
    void shouldApproveBookingIfWaiting() {
        when(mockBookingRepository.findById(1L)).thenReturn(Optional.ofNullable(booking));
        Booking copy = Booking.builder()
                              .id(booking.getId())
                              .booker(booking.getBooker())
                              .start(booking.getStart())
                              .end(booking.getEnd())
                              .item(booking.getItem())
                              .status(Status.APPROVED)
                              .build();
        when(mockBookingRepository.save(any())).thenReturn(copy);
        Booking result = bookingService.updateBooking(booking.getItem().getOwner(), true, booking.getId());
        assertEquals(copy, result);
        assertEquals(Status.APPROVED, result.getStatus());
    }

    @Test
    void shouldRejectBookingIfWaiting() {
        when(mockBookingRepository.findById(1L)).thenReturn(Optional.ofNullable(booking));
        Booking copy = Booking.builder()
                .id(booking.getId())
                .booker(booking.getBooker())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .status(Status.REJECTED)
                .build();
        when(mockBookingRepository.save(any())).thenReturn(copy);
        Booking result = bookingService.updateBooking(booking.getItem().getOwner(), false, booking.getId());
        assertEquals(copy, result);
        assertEquals(Status.REJECTED, result.getStatus());
    }

    @Test
    void shouldThrowExceptionIfBookingNotFound() {
        when(mockBookingRepository.findById(any())).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> bookingService.updateBooking(1L, true, 2L));
        assertEquals("Booking not found!", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionIfUserIsNotAnOwner() {
        when(mockBookingRepository.findById(1L)).thenReturn(Optional.ofNullable(booking));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> bookingService.updateBooking(2L, true, 1L));
        assertEquals("User is not an owner!", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionIfAlreadyApprovedOrRejected() {
        booking.setStatus(Status.APPROVED);
        when(mockBookingRepository.findById(1L)).thenReturn(Optional.ofNullable(booking));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> bookingService.updateBooking(1L, true, 1L));
        assertEquals("booking has been already approved/rejected", ex.getMessage());
        booking.setStatus(Status.REJECTED);
        RuntimeException ex1 = assertThrows(RuntimeException.class, () -> bookingService.updateBooking(1L, true, 1L));
        assertEquals("booking has been already approved/rejected", ex1.getMessage());
    }

    @Test
    void shouldFindBookingById() {
        when(mockBookingRepository.findById(1L)).thenReturn(Optional.ofNullable(booking));
        Booking result = bookingService.getBookingById(1L, booking.getId());
        assertEquals(booking, result);
    }

    @Test
    void shouldThrowExceptionWhenFindByIdAndUserIsNotAnOwnerOrBooker() {
        when(mockBookingRepository.findById(1L)).thenReturn(Optional.ofNullable(booking));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> bookingService.getBookingById(3L, 1L));
        assertEquals("User is not an owner!", ex.getMessage());
    }

    @Test
    void shouldReturnAllBookingsOfBooker() {
        when(mockUserService.getUser(any())).thenReturn(user);
        when(mockBookingRepository.findBookingsByUserId(2L)).thenReturn(List.of(booking));
        List<Booking> bookings = bookingService.getAllByUserId(null, null, 2L, State.WAITING);
        assertEquals(1, bookings.size());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    void shouldReturnEmptyListIfWrongState() {
        when(mockUserService.getUser(any())).thenReturn(user);
        when(mockBookingRepository.findBookingsByUserId(2L)).thenReturn(List.of(booking));
        List<Booking> bookings = bookingService.getAllByUserId(null, null, 2L, State.CURRENT);
        assertTrue(bookings.isEmpty());
    }

    @Test
    void shouldThrowExceptionIfBookerNotFound() {
        when(mockUserService.getUser(any())).thenReturn(null);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> bookingService.getAllByUserId(null, null, 3L, State.WAITING));
        assertEquals("User not found!", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionIfBookerDoesNotHaveBookings() {
        when(mockUserService.getUser(any())).thenReturn(user);
        when(mockBookingRepository.findBookingsByUserId(2L)).thenReturn(new ArrayList<>());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> bookingService.getAllByUserId(null, null, 2L, State.WAITING));
        assertEquals("User does not have bookings!", ex.getMessage());
    }

    @Test
    void shouldFindAllBookingsOfOwner() {
        when(mockUserService.getUser(any())).thenReturn(user);
        when(mockBookingRepository.findBookingsByOwnerId(1L)).thenReturn(List.of(booking));
        List<Booking> bookings = bookingService.getAllByOwnerId(null, null, 1L, State.WAITING);
        assertEquals(1, bookings.size());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    void shouldReturnEmptyListOfOwnerBookingsIfWrongState() {
        when(mockUserService.getUser(any())).thenReturn(user);
        when(mockBookingRepository.findBookingsByOwnerId(1L)).thenReturn(List.of(booking));
        List<Booking> bookings = bookingService.getAllByOwnerId(null, null, 1L, State.REJECTED);
        assertTrue(bookings.isEmpty());
    }

    @Test
    void shouldThrowExceptionIfOwnerNotFound() {
        when(mockUserService.getUser(any())).thenReturn(null);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> bookingService.getAllByOwnerId(null, null, 3L, State.WAITING));
        assertEquals("User not found!", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionIfOwnerDoesNotHaveBookings() {
        when(mockUserService.getUser(any())).thenReturn(user);
        when(mockBookingRepository.findBookingsByUserId(1)).thenReturn(new ArrayList<>());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> bookingService.getAllByOwnerId(null, null, 2L, State.WAITING));
        assertEquals("User does not have bookings!", ex.getMessage());
    }
}