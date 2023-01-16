package ru.practicum.shareit.user.service;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.exception.InvalidEmailException;
import ru.practicum.shareit.exception.UserAlreadyRegisteredException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserDao;

@Service
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    private boolean isEmail(String email) {
        return Pattern.compile("^(.+)@(\\S+)$").matcher(email).matches();
    }

    @Override
    public User addUser(UserDto user)
        throws InvalidEmailException, UserAlreadyRegisteredException {
        String email = user.getEmail();
        if (email == null || !isEmail(email)) {
            throw new InvalidEmailException("Invalid email address");
        }
        if (userDao.findUserByEmail(email)) {
            throw new UserAlreadyRegisteredException("User already registered");
        }
        return userDao.addUser(user);
    }

    @Override
    public void deleteUser(Long userId) {
        userDao.deleteUser(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    @Override
    public User getUser(Long userId) {
        return userDao.getUser(userId);
    }

    @Override
    public User updateUser(Long userId, UserDto userDto)
        throws InvalidEmailException, UserAlreadyRegisteredException {
        if (userDto.getEmail() != null && !isEmail(userDto.getEmail())) {
            throw new InvalidEmailException("Invalid email address");
        }
        if (userDto.getEmail() != null && userDao.findUserByEmail(userDto.getEmail())) {
            throw new UserAlreadyRegisteredException("Email address is used");
        }
        return userDao.updateUser(userId, userDto);
    }
}
