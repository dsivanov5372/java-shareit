package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
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
    void shouldFindBookingByOwnerId() {
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

        List<Booking> bookingList = bookingRepository.findBookingsByOwnerId(firstUser.getId(), PageRequest.of(0, 20));
        assertEquals(booking, bookingList.get(0));
        assertEquals(1, bookingList.size());
    }

    @Test
    void shouldFindBookingByBookerId() {
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

        List<Booking> bookingList = bookingRepository.findAllByBookerId(secondUser.getId(), PageRequest.of(0, 20));
        assertEquals(booking, bookingList.get(0));
        assertEquals(1, bookingList.size());
    }

    @Test
    void shouldFindLastBookingByItemId() {
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
        assertEquals(booking, last);
    }

    @Test
    void shouldFindNextBookingByItemIdAndStartTime() {
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
        assertEquals(booking, last);
    }

    @Test
    void shouldFindBookingByItemIdBookerIdAndStatus() {
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
        assertEquals(booking, result.get());
    }
}