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
    public void shouldAddUserIfNotRegistered() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(userRepository.save(any())).thenReturn(user);
        User result = userService.addUser(UserDto.builder().build());
        assertEquals(result, user);
    }

    @Test
    public void shouldThrowExceptionIfUserIsRegistered() {
        when(userRepository.findByEmail(anyString())).thenReturn(user);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.addUser(UserDto.builder()
                                                                                                    .email("lol")
                                                                                                    .build()));
        assertEquals(ex.getMessage(), "User with this email already registered");
    }

    @Test
    public void shouldDeleteUserIfRegistered() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        userService.deleteUser(Objects.requireNonNull(user).getId());
    }

    @Test
    public void shouldThrowExceptionWhenDeleteUserIsNotExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.deleteUser(1L));
        assertEquals(ex.getMessage(), "User not found!");
    }

    @Test
    public void shouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        List<User> users = userService.getAllUsers();
        assertEquals(users.size(), 1);
        assertEquals(users.get(0), user);
    }

    @Test
    public void shouldFindUserByIdIfExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        User result = userService.getUser(1L);
        assertEquals(result, user);
    }

    @Test
    public void shouldThrowExceptionIfUserNotFoundById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.getUser(3L));
        assertEquals(ex.getMessage(), "User not found!");
    }

    @Test
    public void shouldUpdateUserWithValidFields() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(userRepository.save(any())).thenReturn(user);
        User result = userService.updateUser(1L, UserDto.builder().build());
        assertEquals(result, user);
    }

    @Test
    public void shouldThrowExceptionIfUpdateWithUsedEmail() {
        when(userRepository.findByEmail(anyString())).thenReturn(user);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.updateUser(1L, UserDto.builder()
                                                                                                                  .email(user.getEmail())
                                                                                                                  .build()));
        assertEquals(ex.getMessage(), "Email address is used");
    }
}