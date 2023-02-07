package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private UserServiceImpl userService;
    @Autowired
    private MockMvc mvc;
    private final User user = User.builder().id(1L).name("name").email("null@null.null").build();

    @Test
    void shouldAddUser() throws Exception {
        when(userService.addUser(any())).thenReturn(user);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(UserDto.builder().build()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                        .andExpect(jsonPath("$.name", is(user.getName())))
                        .andExpect(jsonPath("$.email", is(user.getEmail())));
    }

    @Test
    void shouldFindUserById() throws Exception {
        when(userService.getUser(anyLong())).thenReturn(user);

        mvc.perform(get("/users/1"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                        .andExpect(jsonPath("$.name", is(user.getName())))
                        .andExpect(jsonPath("$.email", is(user.getEmail())));
    }

    @Test
    void shouldReturnAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(user));

        mvc.perform(get("/users"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.[0].id", is(user.getId()), Long.class))
                        .andExpect(jsonPath("$.[0].name", is(user.getName())))
                        .andExpect(jsonPath("$.[0].email", is(user.getEmail())));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        when(userService.updateUser(anyLong(), any())).thenReturn(user);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(UserDto.builder().build()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                        .andExpect(jsonPath("$.name", is(user.getName())))
                        .andExpect(jsonPath("$.email", is(user.getEmail())));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        mvc.perform(delete("/users/1"))
                           .andExpect(status().isOk());
    }
}