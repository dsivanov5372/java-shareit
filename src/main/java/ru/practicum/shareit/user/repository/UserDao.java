package ru.practicum.shareit.user.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;

@Component
public class UserDao {
    private Long idSetter = 1L;
    private final Map<Long, User> users = new HashMap<>();

    public User addUser(UserDto userDto) {
        userDto.setId(idSetter++);
        User user = UserMapper.toUser(userDto);
        users.put(user.getId(), user);
        return user;
    }

    public User updateUser(Long userId, UserDto userDto) {
        User user = users.get(userId);
        if (user == null) {
            return null;
        }

        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        return user;
    }

    public void deleteUser(Long userId) {
        users.remove(userId);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User getUser(Long userId) {
        return users.get(userId);
    }

    public boolean findUserByEmail(String email) {
        Optional<User> user = users.values()
                                   .stream()
                                   .filter(obj -> email.equals(obj.getEmail()))
                                   .findFirst();
        return user.isPresent();
    }

    public boolean findUserById(Long id) {
        return users.containsKey(id);
    }
}
