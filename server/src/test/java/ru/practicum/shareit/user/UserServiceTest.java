package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceTest {
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    private final User user = User.builder().id(1L).name("name").email("null@null.null").build();

    @Test
    void shouldAddUserIfNotRegistered() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(userRepository.save(any())).thenReturn(user);
        User result = userService.addUser(UserDto.builder().build());
        assertEquals(user, result);
    }

    @Test
    void shouldThrowExceptionIfUserIsRegistered() {
        when(userRepository.findByEmail(anyString())).thenReturn(user);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.addUser(UserDto.builder()
                                                                                                    .email("lol")
                                                                                                    .build()));
        assertEquals("User with this email already registered", ex.getMessage());
    }

    @Test
    void shouldDeleteUserIfRegistered() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        userService.deleteUser(Objects.requireNonNull(user).getId());
    }

    @Test
    void shouldThrowExceptionWhenDeleteUserIsNotExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.deleteUser(1L));
        assertEquals("User not found!", ex.getMessage());
    }

    @Test
    void shouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        List<User> users = userService.getAllUsers();
        assertEquals(1, users.size());
        assertEquals(user, users.get(0));
    }

    @Test
    void shouldFindUserByIdIfExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        User result = userService.getUser(1L);
        assertEquals(user, result);
    }

    @Test
    void shouldThrowExceptionIfUserNotFoundById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.getUser(3L));
        assertEquals("User not found!", ex.getMessage());
    }

    @Test
    void shouldUpdateUserWithValidFields() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(userRepository.save(any())).thenReturn(user);
        User result = userService.updateUser(1L, UserDto.builder().build());
        assertEquals(user, result);
    }

    @Test
    void shouldThrowExceptionIfUpdateWithUsedEmail() {
        when(userRepository.findByEmail(anyString())).thenReturn(User.builder().id(100L).email(user.getEmail()).build());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.updateUser(1L, UserDto.builder()
                                                                                                                  .email(user.getEmail())
                                                                                                                  .build()));
        assertEquals("Email address is used", ex.getMessage());
    }
}