package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {
    private UserController controller;

    @BeforeEach
    public void createController() {
        controller = new UserController();
    }

    private static Stream<Arguments> validUsers() {
        return Stream.of(
                Arguments.of(User.builder()
                                .email("tester@yandex.ru")
                                .name("Test name")
                                .login("ValidTestLogin")
                                .birthday(LocalDate.of(1964, 6, 11))
                                .build(),
                        "tester@yandex.ru",
                        "Test name",
                        "ValidTestLogin",
                        LocalDate.of(1964, 6, 11),
                        1),
                Arguments.of(User.builder()
                                .email("tester@yandex.ru")
                                .name("")
                                .login("ValidTestLogin")
                                .birthday(LocalDate.of(1942, 12, 4))
                                .build(),
                        "tester@yandex.ru",
                        "ValidTestLogin",
                        "ValidTestLogin",
                        LocalDate.of(1942, 12, 4),
                        1));
    }

    @ParameterizedTest
    @MethodSource("validUsers")
    public void shouldAddUserWithValidFields(User user, String email, String name, String login,
                               LocalDate birthday, int id) throws ValidationException{
        controller.add(user);
        List<User> arr = new ArrayList<>(controller.getAll());

        assertEquals(arr.size(), 1);

        User userFromController = arr.get(0);

        assertEquals(userFromController.getEmail(), email);
        assertEquals(userFromController.getName(), name);
        assertEquals(userFromController.getLogin(), login);
        assertEquals(userFromController.getBirthday(), birthday);
        assertEquals(userFromController.getId(), id);
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

    private static Stream<Arguments> invalidUsers() {
        return Stream.of(
                Arguments.of(User.builder()
                                .name("Test name")
                                .login("ValidTestLogin")
                                .birthday(LocalDate.of(1964, 6, 11))
                                .build(),
                        "Электронная почта не может быть пустой и должна содержать символ @"),
                Arguments.of(User.builder()
                                .email("")
                                .name("Test name")
                                .login("ValidTestLogin")
                                .birthday(LocalDate.of(1964, 6, 11))
                                .build(),
                        "Электронная почта не может быть пустой и должна содержать символ @"),
                Arguments.of(User.builder()
                                .email(" ")
                                .name("Test name")
                                .login("ValidTestLogin")
                                .birthday(LocalDate.of(1964, 6, 11))
                                .build(),
                        "Электронная почта не может быть пустой и должна содержать символ @"),
                Arguments.of(User.builder()
                                .email("yandex.ru")
                                .name("Test name")
                                .login("ValidTestLogin")
                                .birthday(LocalDate.of(1964, 6, 11))
                                .build(),
                        "Электронная почта не может быть пустой и должна содержать символ @"),
                Arguments.of(User.builder()
                        .email("tester@yandex.ru")
                        .name("Test name")
                        .login("")
                        .birthday(LocalDate.of(1964, 6, 11))
                        .build(),
                        "Неправильный формат логина."),
                Arguments.of(User.builder()
                        .email("tester@yandex.ru")
                        .name("Test name")
                        .birthday(LocalDate.of(1964, 6, 11))
                        .build(),
                        "Неправильный формат логина."),
                Arguments.of(User.builder()
                        .email("tester@yandex.ru")
                        .name("Test name")
                        .login("Not Valid Test Login")
                        .birthday(LocalDate.of(1964, 6, 11))
                        .build(),
                        "Неправильный формат логина."),
                Arguments.of(User.builder()
                        .email("tester@yandex.ru")
                        .name("Test name")
                        .login("ValidTestLogin")
                        .birthday(LocalDate.of(2222, 6, 11))
                        .build(),
                        "Дата рождения пользователя не может быть в будущем"));
    }

    @ParameterizedTest
    @MethodSource("invalidUsers")
    public void shouldNotAddFilmWithNotValidFields(User user, String message) {
        ValidationException exception = assertThrows(ValidationException.class, () -> {controller.add(user);});
        assertEquals(message, exception.getMessage());
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
