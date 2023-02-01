package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    private final User firstUser = User.builder().name("null null").email("null@null.null").build();
    private final User secondUser = User.builder().name("null null").email("notNull@null.null").build();
    private Item item = Item.builder().name("test item").description("test item").available(true).build();

    @Test
    public void shouldFindBookingByOwnerId() {
        userRepository.save(firstUser);
        userRepository.save(secondUser);
        item.setOwner(firstUser.getId());
        item = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(secondUser);
        booking.setStatus(Status.WAITING);
        booking = bookingRepository.save(booking);

        List<Booking> bookingList = bookingRepository.findBookingsByOwnerId(firstUser.getId());
        assertEquals(bookingList.get(0), booking);
        assertEquals(bookingList.size(), 1);
    }

    @Test
    public void shouldFindBookingByBookerId() {
        userRepository.save(firstUser);
        userRepository.save(secondUser);
        item.setOwner(firstUser.getId());
        item = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(secondUser);
        booking.setStatus(Status.WAITING);
        booking = bookingRepository.save(booking);

        List<Booking> bookingList = bookingRepository.findBookingsByUserId(secondUser.getId());
        assertEquals(bookingList.get(0), booking);
        assertEquals(bookingList.size(), 1);
    }

    @Test
    public void shouldFindLastBookingByItemId() {
        userRepository.save(firstUser);
        userRepository.save(secondUser);
        item.setOwner(firstUser.getId());
        item = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(secondUser);
        booking.setStatus(Status.WAITING);
        booking = bookingRepository.save(booking);

        Booking last = bookingRepository.findTopBookingByItemIdOrderByStartAsc(item.getId());
        assertEquals(last, booking);
    }

    @Test
    public void shouldFindNextBookingByItemIdAndStartTime() {
        userRepository.save(firstUser);
        userRepository.save(secondUser);
        item.setOwner(firstUser.getId());
        item = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(secondUser);
        booking.setStatus(Status.WAITING);
        booking = bookingRepository.save(booking);

        Booking last = bookingRepository.findFirstBookingByItemIdAndStartAfterOrderByStartAsc(item.getId(),
                                                                                              LocalDateTime.now()
                                                                                                            .minusMinutes(1));
        assertEquals(last, booking);
    }

    @Test
    public void shouldFindBookingByItemIdBookerIdAndStatus() {
        userRepository.save(firstUser);
        userRepository.save(secondUser);
        item.setOwner(firstUser.getId());
        item = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(secondUser);
        booking.setStatus(Status.WAITING);
        booking = bookingRepository.save(booking);

        Optional<Booking> result = bookingRepository.findFirstBookingByItemIdAndBookerIdAndStatusOrderByStartAsc(item.getId(),
                                                                                                                 secondUser.getId(),
                                                                                                                 Status.WAITING);
        assertFalse(result.isEmpty());
        assertEquals(result.get(), booking);
    }
}