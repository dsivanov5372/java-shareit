package ru.practicum.shareit.user.service;

import java.util.List;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.exception.InvalidEmailException;
import ru.practicum.shareit.exception.UserAlreadyRegisteredException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

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
        return userRepository.save(UserMapper.toUser(user));
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUser(Long userId) {
        return userRepository.findById(userId)
                             .orElseThrow(() -> new UserNotFoundException("User not found!"));
    }

    @Override
    public User updateUser(Long userId, UserDto userDto)
        throws InvalidEmailException, UserAlreadyRegisteredException {
        String email = userDto.getEmail();
        if (email != null && !isEmail(email)) {
            throw new InvalidEmailException("Invalid email address");
        }
        if (email != null && userRepository.findByEmail(email) != null) {
            throw new UserAlreadyRegisteredException("Email address is used");
        }

        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (email != null) {
            user.setEmail(email);
        }
        return userRepository.save(user);
    }
}
