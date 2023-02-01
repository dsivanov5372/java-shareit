package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookingControllerTest {
    private final String header = "X-Sharer-User-Id";
    private final User user = User.builder()
                                  .id(1L)
                                  .email("null@null.null")
                                  .name("test user name")
                                  .build();
    private final Item item = Item.builder()
                                  .owner(1L)
                                  .description("item for booking")
                                  .name("item for booking")
                                  .available(true)
                                  .build();
    private BookingDto bookingDto;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void setBookingDto() {
        LocalDateTime time = LocalDateTime.now();
        bookingDto = BookingDto.builder()
                .itemId(item.getId())
                .start(time.plusSeconds(1))
                .end(time.plusSeconds(12))
                .build();
    }

    @Test
    void addBookingAndReturnOkWithCode200() throws Exception {
        Booking booking = Booking.builder()
                                 .id(1L)
                                 .status(Status.WAITING)
                                 .start(bookingDto.getStart())
                                 .end(bookingDto.getEnd())
                                 .item(item)
                                 .booker(user)
                                 .build();
        when(bookingService.addBooking(anyLong(), any())).thenReturn(booking);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(header, 3L)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                        .andExpect(jsonPath("$.status", is(booking.getStatus().toString())));
    }

    @Test
    void setBookingStatusApproveAndReturnOkAndCode200() throws Exception {
        Booking booking = Booking.builder()
                                 .id(1L)
                                 .status(Status.WAITING)
                                 .start(bookingDto.getStart())
                                 .end(bookingDto.getEnd())
                                 .item(item)
                                 .booker(user)
                                 .build();
        booking.setStatus(Status.APPROVED);
        when(bookingService.updateBooking(anyLong(), anyBoolean(), anyLong())).thenReturn(booking);

        mvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(header, 3L)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                        .andExpect(jsonPath("$.status", is(booking.getStatus().toString())));
    }

    @Test
    void getBookingByIdAndReturnOkAndCode200() throws Exception {
        Booking booking = Booking.builder()
                                 .id(1L)
                                 .status(Status.WAITING)
                                 .start(bookingDto.getStart())
                                 .end(bookingDto.getEnd())
                                 .item(item)
                                 .booker(user)
                                 .build();
        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(booking);

        mvc.perform(get("/bookings/1")
                        .header(header, 3L))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                        .andExpect(jsonPath("$.status", is(booking.getStatus().toString())));
    }

    @Test
    void getAllBookingsByBookerIdAndReturnOkAndCode200() throws Exception {
        Booking booking = Booking.builder()
                                .id(1L)
                                .status(Status.WAITING)
                                .start(bookingDto.getStart())
                                .end(bookingDto.getEnd())
                                .item(item)
                                .booker(user)
                                .build();
        when(bookingService.getAllByUserId(anyInt(), anyInt(), anyLong(), any())).thenReturn(List.of(booking));

        mvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "1")
                        .header(header, 3L))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.[0].id", is(booking.getId()), Long.class))
                        .andExpect(jsonPath("$.[0].status", is(booking.getStatus().toString())));
    }

    @Test
    void getAllBookingsByOwnerIdAndReturnOkAndCode200() throws Exception {
        Booking booking = Booking.builder()
                                 .id(1L)
                                 .status(Status.WAITING)
                                 .start(bookingDto.getStart())
                                 .end(bookingDto.getEnd())
                                 .item(item)
                                 .booker(user)
                                 .build();
        when(bookingService.getAllByOwnerId(anyInt(), anyInt(), anyLong(), any())).thenReturn(List.of(booking));

        mvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "1")
                        .header(header, 1L))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.[0].id", is(booking.getId()), Long.class))
                        .andExpect(jsonPath("$.[0].status", is(booking.getStatus().toString())));
    }
}