package ru.practicum.shareit.booking.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

public interface BookingRepository extends JpaRepository<Booking, Long>{
    @Query(value = "SELECT b FROM Booking b WHERE b.item.id IN (SELECT DISTINCT i.id FROM Item i WHERE i.owner = ?1 )" +
    "ORDER BY b.start DESC")
    List<Booking> findBookingsByOwnerId(long ownerId);

    Booking findFirstBookingByItemIdAndStartAfterOrderByStartAsc(long itemId, LocalDateTime start);

    Booking findTopBookingByItemIdOrderByStartAsc(long itemId);

    Optional<Booking> findFirstBookingByItemIdAndBookerIdAndStatusOrderByStartAsc(long itemId, long userId, Status status);

    @Query(value = "SELECT b FROM Booking b WHERE b.booker.id = ?1 ORDER BY b.start DESC")
    List<Booking> findBookingsByUserId(long userId);
}
