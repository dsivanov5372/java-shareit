package ru.practicum.shareit.items;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.CommentDto;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
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

@WebMvcTest(controllers = ItemController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ItemControllerTest {
    private final String header = "X-Sharer-User-Id";
    private final User user = User.builder().name("null").email("null2@null.null").id(2L).build();
    private final Item item = Item.builder().name("item").description("item test").owner(1L).available(true).id(1L).build();
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemServiceImpl itemService;
    @Autowired
    private MockMvc mvc;


    @Test
    void shouldAddItem() throws Exception {
        when(itemService.addItem(any(), anyLong())).thenReturn(item);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(ItemDto.builder().build()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(header, item.getOwner())
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                        .andExpect(jsonPath("$.name", is(item.getName())))
                        .andExpect(jsonPath("$.description", is(item.getDescription())));
    }

    @Test
    void shouldAddComment() throws Exception {
        Comment comment = Comment.builder()
                                 .itemId(item.getId())
                                 .authorId(user.getId())
                                 .created(LocalDateTime.now())
                                 .authorName(user.getName())
                                 .text("comment")
                                 .id(1L)
                                 .build();
        when(itemService.addComment(anyLong(), anyLong(), any())).thenReturn(comment);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(CommentDto.builder().build()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(header, item.getOwner())
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id", is(comment.getId()), Long.class))
                        .andExpect(jsonPath("$.text", is(comment.getText())));
    }

    @Test
    void shouldFindItemById() throws Exception {
        when(itemService.findItemById(anyLong(), anyLong())).thenReturn(item);

        mvc.perform(get("/items/1")
                        .header(header, item.getOwner()))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                        .andExpect(jsonPath("$.name", is(item.getName())))
                        .andExpect(jsonPath("$.description", is(item.getDescription())));
    }

    @Test
    void shouldReturnAllItemsByUserId() throws Exception {
        when(itemService.findAllByUserId(anyInt(), anyInt(), anyLong())).thenReturn(List.of(item));

        mvc.perform(get("/items")
                        .param("from", "0")
                        .param("size", "1")
                        .header(header, item.getOwner()))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.[0].id", is(item.getId()), Long.class))
                        .andExpect(jsonPath("$.[0].name", is(item.getName())))
                        .andExpect(jsonPath("$.[0].description", is(item.getDescription())));
    }

    @Test
    void shouldReturnItemsWithTextInNameOrDescription() throws Exception {
        when(itemService.findAllByText(anyInt(), anyInt(), anyString())).thenReturn(List.of(item));

        mvc.perform(get("/items/search")
                        .header(header, item.getOwner())
                        .param("from", "0")
                        .param("size", "1")
                        .param("text", "item"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.[0].id", is(item.getId()), Long.class))
                        .andExpect(jsonPath("$.[0].name", is(item.getName())))
                        .andExpect(jsonPath("$.[0].description", is(item.getDescription())));
    }

    @Test
    void shouldUpdateItem() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any())).thenReturn(item);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(ItemDto.builder().build()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(header, item.getOwner())
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                        .andExpect(jsonPath("$.name", is(item.getName())))
                        .andExpect(jsonPath("$.description", is(item.getDescription())));
    }
}