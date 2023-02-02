package ru.practicum.shareit.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ItemRequestControllerTest {

    private final String header = "X-Sharer-User-Id";
    private final User user = User.builder().name("null").email("null2@null.null").id(2L).build();
    private final Item item = Item.builder().name("item").description("item test").owner(1L).available(true).id(1L).build();
    private final ItemRequest request = ItemRequest.builder()
                                                   .requestorId(2L)
                                                   .items(List.of(item))
                                                   .description("text")
                                                   .created(LocalDateTime.now())
                                                   .id(1L)
                                                   .build();
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemRequestService requestService;
    @Autowired
    private MockMvc mvc;

    @Test
    void shouldAddRequest() throws Exception {
        when(requestService.addRequest(anyLong(), any())).thenReturn(request);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(ItemRequestDto.builder().description("test").build()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(header, user.getId())
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id", is(request.getId()), Long.class))
                        .andExpect(jsonPath("$.description", is(request.getDescription())))
                        .andExpect(jsonPath("$.requestorId", is(request.getRequestorId()), Long.class));
    }

    @Test
    void shouldReturnAllRequestsByRequestorId() throws Exception {
        when(requestService.getAllByUserId(anyLong())).thenReturn(List.of(request));

        mvc.perform(get("/requests")
                        .header(header, user.getId()))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.[0].id", is(request.getId()), Long.class))
                        .andExpect(jsonPath("$.[0].description", is(request.getDescription())))
                        .andExpect(jsonPath("$.[0].requestorId", is(request.getRequestorId()), Long.class));
    }

    @Test
    void shouldReturnAllRequestsByUserId() throws Exception {
        when(requestService.getAllRequests(anyInt(), anyInt(), anyLong())).thenReturn(List.of(request));

        mvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "1")
                        .header(header, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.[0].id", is(request.getId()), Long.class))
                        .andExpect(jsonPath("$.[0].description", is(request.getDescription())))
                        .andExpect(jsonPath("$.[0].requestorId", is(request.getRequestorId()), Long.class));
    }

    @Test
    void shouldFindRequestById() throws Exception {
        when(requestService.getRequestById(anyLong(), anyLong())).thenReturn(request);

        mvc.perform(get("/requests/1")
                        .header(header, user.getId()))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id", is(request.getId()), Long.class))
                        .andExpect(jsonPath("$.description", is(request.getDescription())))
                        .andExpect(jsonPath("$.requestorId", is(request.getRequestorId()), Long.class));
    }
}