package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {
    private UserController controller;

    @BeforeEach
    public void createController() {
        controller = new UserController();
    }

    @Test
    public void shouldAddUserWithValidFields() {
        User user1 = User.builder()
                .email("tester@yandex.ru")
                .name("Test name")
                .login("ValidTestLogin")
                .birthday(LocalDate.of(1964, 6, 11))
                .build();
        controller.add(user1);

        User user2 = User.builder()
                .email("tester@yandex.ru")
                .name("")
                .login("ValidTestLogin")
                .birthday(LocalDate.of(1942, 12, 4))
                .build();
        controller.add(user2);

        List<User> arr = new ArrayList<>(controller.getAll());

        assertEquals(arr.size(), 2);

        User user1FromController = arr.get(0);
        User user2FromController = arr.get(1);

        assertEquals(user1FromController.getEmail(), user1.getEmail());
        assertEquals(user1FromController.getName(), user1.getName());
        assertEquals(user1FromController.getLogin(), user1.getLogin());
        assertEquals(user1FromController.getBirthday(), user1.getBirthday());
        assertEquals(user1FromController.getId(), 1);

        assertEquals(user2FromController.getEmail(), user2.getEmail());
        assertEquals(user2FromController.getName(), user2.getLogin());
        assertEquals(user2FromController.getLogin(), user2.getLogin());
        assertEquals(user2FromController.getBirthday(), user2.getBirthday());
        assertEquals(user2FromController.getId(), 2);
    }

    @Test
    public void shouldThrowExceptionIfAddUserIdFound() {
        User user = User.builder()
                .email("tester@yandex.ru")
                .name("Test name")
                .login("ValidTestLogin")
                .birthday(LocalDate.of(1964, 6, 11))
                .build();
        controller.add(user);

        User newUser = User.builder()
                .id(1)
                .email("tester@yandex.ru")
                .name("Test name")
                .login("ValidTestLogin")
                .birthday(LocalDate.of(1964, 6, 11))
                .build();

        ValidationException exception = assertThrows(ValidationException.class, () -> {controller.add(newUser);});
        assertEquals("Пользователь с таким id уже существует.", exception.getMessage());
        assertEquals(controller.getAll().size(), 1);
    }

    @Test
    public void shouldThrowExceptionIfAddUserLoginHaveSpace() {
        User user = User.builder()
                .email("tester@yandex.ru")
                .name("Test name")
                .login("Not Valid Test Login")
                .birthday(LocalDate.of(1964, 6, 11))
                .build();
        ValidationException exception = assertThrows(ValidationException.class, () -> {controller.add(user);});
        assertEquals("Неправильный формат логина.", exception.getMessage());
        assertEquals(controller.getAll().size(), 0);
    }

    @Test
    public void shouldUpdateUser() {
        User user = User.builder()
                .email("tester@yandex.ru")
                .name("Test name")
                .login("ValidTestLogin")
                .birthday(LocalDate.of(1964, 6, 11))
                .build();
        controller.add(user);

        User newUser = User.builder()
                .email("update@yandex.ru")
                .name("Update test name")
                .login("UpdateValidTestLogin")
                .birthday(LocalDate.of(2000, 1, 1))
                .id(1)
                .build();
        controller.update(newUser);

        List<User> arr = new ArrayList<>(controller.getAll());

        assertEquals(arr.size(), 1);

        User userFromController = arr.get(0);

        assertEquals(userFromController.getEmail(), newUser.getEmail());
        assertEquals(userFromController.getName(), newUser.getName());
        assertEquals(userFromController.getLogin(), newUser.getLogin());
        assertEquals(userFromController.getBirthday(), newUser.getBirthday());
        assertEquals(userFromController.getId(), newUser.getId());
    }

    @Test
    public void shouldThrowExceptionIfUserIdNotFound() {
        User user = User.builder()
                .email("tester@yandex.ru")
                .name("Test name")
                .login("ValidTestLogin")
                .birthday(LocalDate.of(1964, 6, 11))
                .build();
        controller.add(user);

        User newUser = User.builder()
                .email("update@yandex.ru")
                .name("Update test name")
                .login("UpdateValidTestLogin")
                .birthday(LocalDate.of(2000, 1, 1))
                .id(999)
                .build();

        ValidationException exception = assertThrows(ValidationException.class, () -> {controller.update(newUser);});
        assertEquals("Пользователя с таким id не существует.", exception.getMessage());
        assertEquals(controller.getAll().size(), 1);
    }
}
