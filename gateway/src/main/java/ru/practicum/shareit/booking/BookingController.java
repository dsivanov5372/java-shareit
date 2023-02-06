package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.exception.InvalidStateException;
import ru.practicum.shareit.exception.PageSizeException;

@Controller
@AllArgsConstructor
@RequestMapping("/bookings")
public class BookingController {
	private final String header = "X-Sharer-User-Id";
	private final BookingClient bookingClient;

	@GetMapping
	public ResponseEntity<Object> getBookingsByState(@RequestHeader(header) Long userId,
						@RequestParam(name = "state", defaultValue = "ALL") String state,
						@RequestParam(value = "from", defaultValue = "0") Integer from,
						@RequestParam(value = "size", defaultValue = "20") Integer size) {
		checkParams(from, size);
		checkState(state);
		return bookingClient.getBookingsByState(userId, State.valueOf(state), from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getBookingsByOwner(@RequestHeader(header) Long userId,
						@RequestParam(name = "state", defaultValue = "ALL") String state,
						@RequestParam(value = "from", defaultValue = "0") Integer from,
						@RequestParam(value = "size", defaultValue = "20") Integer size) {
		checkParams(from, size);
		checkState(state);
		return bookingClient.getBookingsByOwner(userId, State.valueOf(state), from, size);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader(header) Long userId,
											 @PathVariable Long bookingId) {
		return bookingClient.getBooking(userId, bookingId);
	}

	@PostMapping
	public ResponseEntity<Object> createBooking(@RequestHeader(header) Long userId,
										 		@RequestBody BookingDto bookingRequestDto) {
		return bookingClient.createBooking(userId, bookingRequestDto);
	}


	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> updateBookingState(@RequestHeader(header) Long userId,
													 @PathVariable Long bookingId,
													 @RequestParam Boolean approved) {
		return bookingClient.updateBookingState(userId, bookingId, approved);
	}

	@DeleteMapping("/{bookingId}")
	public void deleteBooking(@PathVariable Long bookingId) {
		bookingClient.deleteBooking(bookingId);
	}

	private void checkParams(Integer from, Integer size) {
        if (from < 0 || size < 1) {
            throw new PageSizeException("Invalid pagination params!");
        }
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