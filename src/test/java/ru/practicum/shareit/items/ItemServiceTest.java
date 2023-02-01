package ru.practicum.shareit.items;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.dto.CommentDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemServiceTest {
    @Autowired
    private final ItemService service;
    @MockBean
    private final ItemRepository itemRepository;
    @MockBean
    private final UserRepository userRepository;
    @MockBean
    private final BookingRepository bookingRepository;
    @MockBean
    private final CommentRepository commentRepository;
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

    @Test
    public void shouldAddItemWithValidThings() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
        when(itemRepository.save(any())).thenReturn(item);
        ItemDto itemDto = ItemDto.builder().name("test").description("test").available(true).build();
        Item result = service.addItem(itemDto, Objects.requireNonNull(user).getId());
        assertEquals(result, item);
    }

    @Test
    public void shouldThrowExceptionIfInvalidFields() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.addItem(ItemDto.builder().build(), 1L));
        assertEquals(ex.getMessage(), "Invalid item fields");
    }

    @Test
    public void shouldReturnAllItemOfOwner() {
        when(itemRepository.findByOwnerOrderById(anyLong())).thenReturn(List.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        Booking last = Booking.builder().id(1L).booker(User.builder().id(2L).build()).build();
        Booking next = Booking.builder().id(2L).booker(User.builder().id(2L).build()).build();
        Comment comment = Comment.builder().build();
        when(bookingRepository.findTopBookingByItemIdOrderByStartAsc(1L)).thenReturn(last);
        when(bookingRepository.findFirstBookingByItemIdAndStartAfterOrderByStartAsc(anyLong(), any())).thenReturn(next);
        when(commentRepository.findCommentsByItemId(any())).thenReturn(List.of(comment));
        List<Item> items = service.findAllByUserId(null, null, Objects.requireNonNull(item).getOwner());
        assertEquals(items.size(), 1);
        assertEquals(items.get(0), item);
        item.setLastBooking(null);
        item.setNextBooking(null);
        item.setComments(new ArrayList<>());
    }

    @Test
    public void shouldThrowExceptionIfUserNotFoundWhenGetAllItems() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.findAllByUserId(null, null, 3L));
        assertEquals(ex.getMessage(), "User not found!");
    }

    @Test
    public void shouldFindItemById() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        Booking last = Booking.builder().id(1L).booker(User.builder().id(2L).build()).build();
        Booking next = Booking.builder().id(2L).booker(User.builder().id(2L).build()).build();
        Comment comment = Comment.builder().id(1L).build();
        when(bookingRepository.findTopBookingByItemIdOrderByStartAsc(1L)).thenReturn(last);
        when(bookingRepository.findFirstBookingByItemIdAndStartAfterOrderByStartAsc(anyLong(), any())).thenReturn(next);
        when(commentRepository.findCommentsByItemId(any())).thenReturn(List.of(comment));
        Item result = service.findItemById(Objects.requireNonNull(user).getId(), item.getId());
        assertEquals(result, item);
        item.setLastBooking(null);
        item.setNextBooking(null);
        item.setComments(new ArrayList<>());
    }

    @Test
    public void shouldThrowExceptionIfItemNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.findItemById(3L, 3L));
        assertEquals(ex.getMessage(), "Item nof found!");
    }

    @Test
    public void shouldReturnItemsWithTextInNameOrDescription() {
        when(itemRepository.searchItemByText("est")).thenReturn(List.of(item));
        List<Item> items = service.findAllByText(null, null, "est");
        assertEquals(items.size(), 1);
        assertEquals(items.get(0), item);
    }

    @Test
    public void shouldReturnEmptyListIfNoText() {
        List<Item> items = service.findAllByText(null, null, " ");
        assertTrue(items.isEmpty());
    }

    @Test
    public void shouldUpdateUpdateItemIfUserIsOwner() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.save(any())).thenReturn(item);
        Item result = service.updateItem(Objects.requireNonNull(item).getOwner(), item.getId(), ItemDto.builder().build());
        assertEquals(result, item);
    }

    @Test
    public void shouldThrowExceptionIfUserIsNotAnOwner() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.updateItem(3L, 1L, ItemDto.builder().build()));
        assertEquals(ex.getMessage(), "User is not an owner!");
    }

    @Test
    public void shouldAddCommentIfItemIsBooked() {
        CommentDto commentDto = CommentDto.builder().text("text").build();
        Booking booking = Booking.builder().start(LocalDateTime.now()).build();
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findFirstBookingByItemIdAndBookerIdAndStatusOrderByStartAsc(anyLong(), anyLong(), any()))
                .thenReturn(Optional.ofNullable(booking));
        Comment comment = Comment.builder().build();
        when(commentRepository.save(any())).thenReturn(comment);
        Comment result = service.addComment(1L, 1L, commentDto);
        assertEquals(result, comment);
    }

    @Test
    public void shouldThrowExceptionIfEmptyComment() {
        CommentDto commentDto = CommentDto.builder().text(" ").build();
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.addComment(1L, 1L, commentDto));
        assertEquals(ex.getMessage(), "Empty comment!");
    }

    @Test
    public void shouldThrowExceptionIfItemIsNotBookedYet() {
        CommentDto commentDto = CommentDto.builder().text("text").build();
        Booking booking = Booking.builder().start(LocalDateTime.now().plusDays(1)).build();
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findFirstBookingByItemIdAndBookerIdAndStatusOrderByStartAsc(anyLong(), anyLong(), any()))
                .thenReturn(Optional.ofNullable(booking));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.addComment(1L, 1L, commentDto));
        assertEquals(ex.getMessage(), "Item not booked yet!");
    }
}