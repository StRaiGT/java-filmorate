package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.IllegalAddFriendException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {
    private UserController controller;

    @BeforeEach
    public void createController() {
        controller = new UserController(new UserService(new InMemoryUserStorage()));
    }

    @Test
    public void shouldAddUserWithValidFields() {
        User user1 = User.builder()
                .email("tester@yandex.ru")
                .name("Test name")
                .login("ValidTestLogin")
                .birthday(LocalDate.of(1964, 6, 11))
                .build();
        controller.create(user1);

        User user2 = User.builder()
                .email("tester@yandex.ru")
                .name("")
                .login("ValidTestLogin")
                .birthday(LocalDate.of(1942, 12, 4))
                .build();
        controller.create(user2);

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
        controller.create(user);

        User newUser = User.builder()
                .id(1)
                .email("tester@yandex.ru")
                .name("Test name")
                .login("ValidTestLogin")
                .birthday(LocalDate.of(1964, 6, 11))
                .build();

        UserAlreadyExistException exception = assertThrows(UserAlreadyExistException.class, () -> {controller.create(newUser);});
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
        ValidationException exception = assertThrows(ValidationException.class, () -> {controller.create(user);});
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
        controller.create(user);

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
        controller.create(user);

        User newUser = User.builder()
                .email("update@yandex.ru")
                .name("Update test name")
                .login("UpdateValidTestLogin")
                .birthday(LocalDate.of(2000, 1, 1))
                .id(999)
                .build();

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {controller.update(newUser);});
        assertEquals("Пользователя с таким id не существует.", exception.getMessage());
        assertEquals(controller.getAll().size(), 1);
    }

    @Test
    public void shouldGetAllUsers() {
        User user1 = User.builder()
                .id(1)
                .email("tester1@yandex.ru")
                .name("Test name 1")
                .login("ValidTestLogin1")
                .birthday(LocalDate.of(1964, 6, 11))
                .build();
        controller.create(user1);

        User user2 = User.builder()
                .id(2)
                .email("tester2@yandex.ru")
                .name("Test name 2")
                .login("ValidTestLogin2")
                .birthday(LocalDate.of(1984, 9, 4))
                .build();
        controller.create(user2);
        List<User> arr = new ArrayList<>(controller.getAll());

        assertEquals(arr.size(), 2);
        assertEquals(arr.get(0), user1);
        assertEquals(arr.get(1), user2);
    }

    @Test
    public void shouldGetEmptyIfNoFilms() {
        List<User> arr = new ArrayList<>(controller.getAll());

        assertEquals(arr.size(), 0);
    }

    @Test
    public void shouldGetUserById() {
        User user = User.builder()
                .id(1)
                .email("tester@yandex.ru")
                .name("Test name")
                .login("ValidTestLogin")
                .birthday(LocalDate.of(1964, 6, 11))
                .build();
        controller.create(user);
        User userFromController = controller.getUserById(1);

        assertEquals(userFromController, user);
    }

    @Test
    public void shouldThrowExceptionIfFilmIdNotFound() {
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {controller.getUserById(999);});
        assertEquals("Пользователя с таким id не существует.", exception.getMessage());
    }

    @Test
    public void shouldAddFriend() {
        User user1 = User.builder()
                .id(1)
                .email("tester1@yandex.ru")
                .name("Test name 1")
                .login("ValidTestLogin1")
                .birthday(LocalDate.of(1964, 6, 11))
                .build();
        controller.create(user1);

        User user2 = User.builder()
                .id(2)
                .email("tester2@yandex.ru")
                .name("Test name 2")
                .login("ValidTestLogin2")
                .birthday(LocalDate.of(1984, 9, 4))
                .build();
        controller.create(user2);

        controller.addFriend(1, 2);

        assertEquals(controller.getUserFriends(1).size(), 1);
        assertEquals(controller.getUserFriends(1).get(0).getId(), 2);
        assertEquals(controller.getUserFriends(2).size(), 1);
        assertEquals(controller.getUserFriends(2).get(0).getId(), 1);
    }

    @Test
    public void shouldThrowExceptionAddSelfInFriend() {
        User user1 = User.builder()
                .id(1)
                .email("tester1@yandex.ru")
                .name("Test name 1")
                .login("ValidTestLogin1")
                .birthday(LocalDate.of(1964, 6, 11))
                .build();
        controller.create(user1);

        User user2 = User.builder()
                .id(2)
                .email("tester2@yandex.ru")
                .name("Test name 2")
                .login("ValidTestLogin2")
                .birthday(LocalDate.of(1984, 9, 4))
                .build();
        controller.create(user2);

        IllegalAddFriendException exception = assertThrows(IllegalAddFriendException.class, () -> {controller.addFriend(1, 1);});
        assertEquals("Пользователь не может добавить в друзья себя самого.", exception.getMessage());
    }

    @Test
    public void shouldRemoveFriend() {
        User user1 = User.builder()
                .id(1)
                .email("tester1@yandex.ru")
                .name("Test name 1")
                .login("ValidTestLogin1")
                .birthday(LocalDate.of(1964, 6, 11))
                .build();
        controller.create(user1);

        User user2 = User.builder()
                .id(2)
                .email("tester2@yandex.ru")
                .name("Test name 2")
                .login("ValidTestLogin2")
                .birthday(LocalDate.of(1984, 9, 4))
                .build();
        controller.create(user2);

        controller.addFriend(1, 2);
        controller.removeFriend(1, 2);

        assertEquals(controller.getUserFriends(1).size(), 0);
        assertEquals(controller.getUserFriends(2).size(), 0);
    }

    @Test
    public void shouldThrowExceptionRemoveNotFriend() {
        User user1 = User.builder()
                .id(1)
                .email("tester1@yandex.ru")
                .name("Test name 1")
                .login("ValidTestLogin1")
                .birthday(LocalDate.of(1964, 6, 11))
                .build();
        controller.create(user1);

        User user2 = User.builder()
                .id(2)
                .email("tester2@yandex.ru")
                .name("Test name 2")
                .login("ValidTestLogin2")
                .birthday(LocalDate.of(1984, 9, 4))
                .build();
        controller.create(user2);

        UsersNotFriendsException exception = assertThrows(UsersNotFriendsException.class, () -> {controller.removeFriend(1, 1);});
        assertEquals("Пользователи не являются друзьями.", exception.getMessage());
    }

    @Test
    public void shouldGetUserFriends() {
        User user1 = User.builder()
                .id(1)
                .email("tester1@yandex.ru")
                .name("Test name 1")
                .login("ValidTestLogin1")
                .birthday(LocalDate.of(1964, 6, 11))
                .build();
        controller.create(user1);

        User user2 = User.builder()
                .id(2)
                .email("tester2@yandex.ru")
                .name("Test name 2")
                .login("ValidTestLogin2")
                .birthday(LocalDate.of(1984, 9, 4))
                .build();
        controller.create(user2);

        User user3 = User.builder()
                .id(3)
                .email("tester2@yandex.ru")
                .name("Test name 2")
                .login("ValidTestLogin2")
                .birthday(LocalDate.of(1984, 9, 4))
                .build();
        controller.create(user3);

        controller.addFriend(1, 2);
        controller.addFriend(1, 3);

        assertEquals(controller.getUserFriends(1).size(), 2);
        assertEquals(controller.getUserFriends(1).get(0), user2);
        assertEquals(controller.getUserFriends(1).get(1), user3);
        assertEquals(controller.getUserFriends(2).size(), 1);
        assertEquals(controller.getUserFriends(2).get(0), user1);
        assertEquals(controller.getUserFriends(3).size(), 1);
        assertEquals(controller.getUserFriends(3).get(0), user1);
    }

    @Test
    public void shouldGetUserCommonFriends() {
        User user1 = User.builder()
                .id(1)
                .email("tester1@yandex.ru")
                .name("Test name 1")
                .login("ValidTestLogin1")
                .birthday(LocalDate.of(1964, 6, 11))
                .build();
        controller.create(user1);

        User user2 = User.builder()
                .id(2)
                .email("tester2@yandex.ru")
                .name("Test name 2")
                .login("ValidTestLogin2")
                .birthday(LocalDate.of(1984, 9, 4))
                .build();
        controller.create(user2);

        User user3 = User.builder()
                .id(3)
                .email("tester2@yandex.ru")
                .name("Test name 2")
                .login("ValidTestLogin2")
                .birthday(LocalDate.of(1984, 9, 4))
                .build();
        controller.create(user3);

        controller.addFriend(1, 2);
        controller.addFriend(1, 3);
        List<User> listUser = controller.getUserCommonFriends(2, 3);

        assertEquals(listUser.size(), 1);
        assertEquals(listUser.get(0), user1);
    }
}
