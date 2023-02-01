package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldFindUserByEmail() {
        User user = User.builder().id(1L).name("name").email("null@null.null").build();
        userRepository.save(user);
        User result = userRepository.findByEmail("null@null.null");
        assertEquals(result, user);
    }
}