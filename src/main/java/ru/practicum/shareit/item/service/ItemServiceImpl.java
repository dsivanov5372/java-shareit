package ru.practicum.shareit.item.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.dto.CommentDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EmptyCommentException;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.InvalidCommentDateException;
import ru.practicum.shareit.exception.InvalidItemRequestException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.BookingInfo;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public void checkUserId(Long userId) throws UserNotFoundException {
        if (userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException("User not found!");
        }
    }

    @Override
    public Item addItem(ItemDto item, Long userId)
        throws UserNotFoundException, InvalidItemRequestException {
        checkUserId(userId);
        if (item.getName() == null || item.getName().isBlank() ||
            item.getDescription() == null || item.getDescription().isBlank() ||
            item.getAvailable() == null) {
            throw new InvalidItemRequestException("Invalid item fields");
        }
        return itemRepository.save(ItemMapper.toItem(item, userId));
    }

    @Override
    public List<Item> findAllByUserId(Long userId) throws UserNotFoundException {
        checkUserId(userId);

        List<Item> items = itemRepository.findByOwnerOrderById(userId);
        for (Item item : items) {
            Booking last = bookingRepository.findTopBookingByItemIdOrderByStartAsc(item.getId());
            Booking next = bookingRepository.findFirstBookingByItemIdAndStartAfterOrderByStartAsc(item.getId(), LocalDateTime.now());
            if (last != null) {
                item.setLastBooking(BookingInfo.builder().id(last.getId()).bookerId(last.getBooker().getId()).build());
            }
            if (next != null) {
                item.setNextBooking(BookingInfo.builder().id(next.getId()).bookerId(next.getBooker().getId()).build());
            }

            item.setComments(commentRepository.findCommentsByItemId(item.getId()));
        }

        return items;
    }

    @Override
    public Item findItemById(Long userId, Long itemId) throws UserNotFoundException {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Item nof found!"));

        if (item.getOwner() == userId) {
            Booking last = bookingRepository.findTopBookingByItemIdOrderByStartAsc(itemId);
            Booking next = bookingRepository.findFirstBookingByItemIdAndStartAfterOrderByStartAsc(itemId, LocalDateTime.now());
            if (last != null) {
                item.setLastBooking(BookingInfo.builder().id(last.getId()).bookerId(last.getBooker().getId()).build());
            }
            if (next != null) {
                item.setNextBooking(BookingInfo.builder().id(next.getId()).bookerId(next.getBooker().getId()).build());
            }
        }
        item.setComments(commentRepository.findCommentsByItemId(item.getId()));
        for (Comment comment : item.getComments()) {
            comment.setAuthorName(userRepository.findById(comment.getId()).get().getName());
        }

        return item;
    }

    @Override
    public List<Item> findAllByText(String text) {
        return itemRepository.searchItemByText(text);
    }

    @Override
    public Item updateItem(Long userId, Long itemId, ItemDto itemDto) throws UserNotFoundException {
        checkUserId(userId);

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Item nof found!"));
        if (item.getOwner() != userId) {
            throw new UserNotFoundException("User is not an owner!");
        }

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        item = itemRepository.save(item);

        return item;
    }

    @Override
    public Comment addComment(Long userId, Long itemId, CommentDto comment) {
        if (comment.getText() == null || comment.getText().isBlank()) {
            throw new EmptyCommentException("Empty comment!");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found!"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Item not found!"));
        Booking booking = bookingRepository.findFirstBookingByItemIdAndBookerIdAndStatusOrderByStartAsc(itemId, userId, Status.APPROVED)
                                           .orElseThrow(() -> new BookingException("Booking not found!"));

        if (booking.getStart().isAfter(LocalDateTime.now())) {
            throw new InvalidCommentDateException("Item not booked yet!");
        }

        Comment toSave = Comment.builder()
                                .authorId(userId)
                                .authorName(user.getName())
                                .created(LocalDateTime.now())
                                .itemId(item.getId())
                                .text(comment.getText())
                                .build();
        return commentRepository.save(toSave);
    }
}