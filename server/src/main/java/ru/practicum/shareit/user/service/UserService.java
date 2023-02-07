package ru.practicum.shareit.user.service;

import java.util.List;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public interface UserService {
    User addUser(UserDto user);

    User updateUser(Long userId, UserDto userDto);

    void deleteUser(Long userId);

    List<User> getAllUsers();

    User getUser(Long userId);
}
