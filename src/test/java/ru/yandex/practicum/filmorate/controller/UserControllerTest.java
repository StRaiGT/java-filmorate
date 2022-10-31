package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
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
        ArrayList<User> arr = new ArrayList<>(controller.getAll());

        assertEquals(arr.size(), 1);
        assertEquals(arr.get(0).getEmail(), email);
        assertEquals(arr.get(0).getName(), name);
        assertEquals(arr.get(0).getLogin(), login);
        assertEquals(arr.get(0).getBirthday(), birthday);
        assertEquals(arr.get(0).getId(), id);
    }

    @Test
    public void shouldThrowsExceptionIfNotValidLogin() {
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

    private static Stream<Arguments> invalidUsers() {
        return Stream.of(
                Arguments.of(User.builder()
                        .email("yandex.ru")
                        .name("Test name")
                        .login("ValidTestLogin")
                        .birthday(LocalDate.of(1964, 6, 11))
                        .build()),
                Arguments.of(User.builder()
                        .email("tester@yandex.ru")
                        .name("Test name")
                        .login("")
                        .birthday(LocalDate.of(1964, 6, 11))
                        .build()),
                Arguments.of(User.builder()
                        .email("tester@yandex.ru")
                        .name("Test name")
                        .birthday(LocalDate.of(1964, 6, 11))
                        .build()),
                Arguments.of(User.builder()
                        .email("tester@yandex.ru")
                        .name("Test name")
                        .login("ValidTestLogin")
                        .birthday(LocalDate.of(2222, 6, 11))
                        .build()));
    }

    @ParameterizedTest
    @MethodSource("invalidUsers")
    public void shouldNotAddFilmWithNotValidFields(User user) {
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

        ArrayList<User> arr = new ArrayList<>(controller.getAll());

        assertEquals(arr.get(0).getEmail(), "update@yandex.ru");
        assertEquals(arr.get(0).getName(), "Update test name");
        assertEquals(arr.get(0).getLogin(), "UpdateValidTestLogin");
        assertEquals(arr.get(0).getBirthday(), LocalDate.of(2000, 1, 1));
        assertEquals(arr.get(0).getId(), 1);
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
    }
}
