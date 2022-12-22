package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.IllegalAddFriendException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerTest {
    private final UserController userController;

    @Test
    public void shouldAddUserWithValidFields() {
        User user1 = User.builder()
                .email("tester1@yandex.ru")
                .name("Test name 1")
                .login("ValidTestLogin1")
                .birthday(LocalDate.of(1964, 6, 11))
                .build();
        userController.createUser(user1);

        User user2 = User.builder()
                .email("tester2@yandex.ru")
                .name("")
                .login("ValidTestLogin2")
                .birthday(LocalDate.of(1942, 12, 4))
                .build();
        userController.createUser(user2);

        List<User> arr = new ArrayList<>(userController.getAllUsers());

        assertEquals(arr.size(), 2);

        User user1FromController = arr.get(0);
        User user2FromController = arr.get(1);

        assertEquals(user1FromController.getId(), 1);
        assertEquals(user1FromController.getEmail(), user1.getEmail());
        assertEquals(user1FromController.getName(), user1.getName());
        assertEquals(user1FromController.getLogin(), user1.getLogin());
        assertEquals(user1FromController.getBirthday(), user1.getBirthday());

        assertEquals(user2FromController.getId(), 2);
        assertEquals(user2FromController.getEmail(), user2.getEmail());
        assertEquals(user2FromController.getName(), user2.getLogin());
        assertEquals(user2FromController.getLogin(), user2.getLogin());
        assertEquals(user2FromController.getBirthday(), user2.getBirthday());
    }

    @Test
    public void shouldThrowExceptionIfAddUserLoginHaveSpace() {
        User user = User.builder()
                .email("tester@yandex.ru")
                .name("Test name")
                .login("Not Valid Test Login")
                .birthday(LocalDate.of(1964, 6, 11))
                .build();
        ValidationException exception = assertThrows(ValidationException.class, () -> userController.createUser(user));
        assertEquals("Неправильный формат логина.", exception.getMessage());
        assertEquals(userController.getAllUsers().size(), 0);
    }

    @Test
    public void shouldUpdateUser() {
        User user = User.builder()
                .id(1)
                .email("tester@yandex.ru")
                .name("Test name")
                .login("ValidTestLogin")
                .birthday(LocalDate.of(1964, 6, 11))
                .build();
        userController.createUser(user);

        User newUser = User.builder()
                .id(1)
                .email("update@yandex.ru")
                .name("Update test name")
                .login("UpdateValidTestLogin")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        userController.updateUser(newUser);

        List<User> arr = new ArrayList<>(userController.getAllUsers());

        assertEquals(arr.size(), 1);

        User userFromController = arr.get(0);

        assertEquals(userFromController.getId(), newUser.getId());
        assertEquals(userFromController.getEmail(), newUser.getEmail());
        assertEquals(userFromController.getName(), newUser.getName());
        assertEquals(userFromController.getLogin(), newUser.getLogin());
        assertEquals(userFromController.getBirthday(), newUser.getBirthday());
    }

    @Test
    public void shouldThrowExceptionIfUpdateUserIdNotFound() {
        User user = User.builder()
                .id(1)
                .email("tester@yandex.ru")
                .name("Test name")
                .login("ValidTestLogin")
                .birthday(LocalDate.of(1964, 6, 11))
                .build();
        userController.createUser(user);

        User newUser = User.builder()
                .id(999)
                .email("update@yandex.ru")
                .name("Update test name")
                .login("UpdateValidTestLogin")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userController.updateUser(newUser));
        assertEquals("Пользователя с таким id не существует.", exception.getMessage());
        assertEquals(userController.getAllUsers().size(), 1);
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
        userController.createUser(user1);

        User user2 = User.builder()
                .id(2)
                .email("tester2@yandex.ru")
                .name("Test name 2")
                .login("ValidTestLogin2")
                .birthday(LocalDate.of(1984, 9, 4))
                .build();
        userController.createUser(user2);
        List<User> arr = new ArrayList<>(userController.getAllUsers());

        assertEquals(arr.size(), 2);

        User userFromController1 = arr.get(0);
        User userFromController2 = arr.get(1);

        assertEquals(userFromController1.getId(), user1.getId());
        assertEquals(userFromController1.getEmail(), user1.getEmail());
        assertEquals(userFromController1.getLogin(), user1.getLogin());
        assertEquals(userFromController1.getName(), user1.getName());
        assertEquals(userFromController1.getBirthday(), user1.getBirthday());

        assertEquals(userFromController2.getId(), user2.getId());
        assertEquals(userFromController2.getEmail(), user2.getEmail());
        assertEquals(userFromController2.getLogin(), user2.getLogin());
        assertEquals(userFromController2.getName(), user2.getName());
        assertEquals(userFromController2.getBirthday(), user2.getBirthday());
    }

    @Test
    public void shouldGetEmptyIfNoUsers() {
        List<User> arr = new ArrayList<>(userController.getAllUsers());

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
        userController.createUser(user);
        User userFromController = userController.getUserById(1);

        assertEquals(userFromController.getId(), user.getId());
        assertEquals(userFromController.getEmail(), user.getEmail());
        assertEquals(userFromController.getLogin(), user.getLogin());
        assertEquals(userFromController.getName(), user.getName());
        assertEquals(userFromController.getBirthday(), user.getBirthday());
    }

    @Test
    public void shouldThrowExceptionIfUserIdNotFound() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userController.getUserById(999));
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
        userController.createUser(user1);

        User user2 = User.builder()
                .id(2)
                .email("tester2@yandex.ru")
                .name("Test name 2")
                .login("ValidTestLogin2")
                .birthday(LocalDate.of(1984, 9, 4))
                .build();
        userController.createUser(user2);

        userController.addFriend(1, 2);
        List<User> friends1 = userController.getUserFriends(1);
        List<User> friends2 = userController.getUserFriends(2);

        assertEquals(friends1.size(), 1);
        assertEquals(friends2.size(), 0);

        User userFromController = userController.getUserById(2);

        assertEquals(userFromController.getId(), user2.getId());
        assertEquals(userFromController.getEmail(), user2.getEmail());
        assertEquals(userFromController.getLogin(), user2.getLogin());
        assertEquals(userFromController.getName(), user2.getName());
        assertEquals(userFromController.getBirthday(), user2.getBirthday());
    }

    @Test
    public void shouldThrowExceptionIfAddFriendNotFound() {
        User user1 = User.builder()
                .id(1)
                .email("tester1@yandex.ru")
                .name("Test name 1")
                .login("ValidTestLogin1")
                .birthday(LocalDate.of(1964, 6, 11))
                .build();
        userController.createUser(user1);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userController.addFriend(1, 2));
        assertEquals("Пользователя с таким id не существует.", exception.getMessage());
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
        userController.createUser(user1);

        IllegalAddFriendException exception = assertThrows(IllegalAddFriendException.class, () -> userController.addFriend(1, 1));
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
        userController.createUser(user1);

        User user2 = User.builder()
                .id(2)
                .email("tester2@yandex.ru")
                .name("Test name 2")
                .login("ValidTestLogin2")
                .birthday(LocalDate.of(1984, 9, 4))
                .build();
        userController.createUser(user2);

        userController.addFriend(1, 2);

        List<User> friends1 = userController.getUserFriends(1);
        List<User> friends2 = userController.getUserFriends(2);

        assertEquals(friends1.size(), 1);
        assertEquals(friends2.size(), 0);

        User userFromController1 = friends1.get(0);

        assertEquals(userFromController1.getId(), user2.getId());
        assertEquals(userFromController1.getEmail(), user2.getEmail());
        assertEquals(userFromController1.getLogin(), user2.getLogin());
        assertEquals(userFromController1.getName(), user2.getName());
        assertEquals(userFromController1.getBirthday(), user2.getBirthday());

        userController.removeFriend(1, 2);
        friends1 = userController.getUserFriends(1);
        friends2 = userController.getUserFriends(2);

        assertEquals(friends1.size(), 0);
        assertEquals(friends2.size(), 0);
    }

    @Test
    public void shouldRemoveNotFriend() {
        User user1 = User.builder()
                .id(1)
                .email("tester1@yandex.ru")
                .name("Test name 1")
                .login("ValidTestLogin1")
                .birthday(LocalDate.of(1964, 6, 11))
                .build();
        userController.createUser(user1);

        User user2 = User.builder()
                .id(2)
                .email("tester2@yandex.ru")
                .name("Test name 2")
                .login("ValidTestLogin2")
                .birthday(LocalDate.of(1984, 9, 4))
                .build();
        userController.createUser(user2);

        List<User> friends1 = userController.getUserFriends(1);
        List<User> friends2 = userController.getUserFriends(2);

        assertEquals(friends1.size(), 0);
        assertEquals(friends2.size(), 0);

        userController.removeFriend(1, 2);
        friends1 = userController.getUserFriends(1);
        friends2 = userController.getUserFriends(2);

        assertEquals(friends1.size(), 0);
        assertEquals(friends2.size(), 0);
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
        userController.createUser(user1);

        User user2 = User.builder()
                .id(2)
                .email("tester2@yandex.ru")
                .name("Test name 2")
                .login("ValidTestLogin2")
                .birthday(LocalDate.of(1984, 9, 4))
                .build();
        userController.createUser(user2);

        User user3 = User.builder()
                .id(3)
                .email("tester3@yandex.ru")
                .name("Test name 3")
                .login("ValidTestLogin3")
                .birthday(LocalDate.of(1984, 9, 4))
                .build();
        userController.createUser(user3);

        userController.addFriend(1, 2);
        userController.addFriend(1, 3);
        List<User> friends = userController.getUserFriends(1);

        assertEquals(friends.size(), 2);

        User userFromController1 = friends.get(0);
        User userFromController2 = friends.get(1);

        assertEquals(userFromController1.getId(), user2.getId());
        assertEquals(userFromController1.getEmail(), user2.getEmail());
        assertEquals(userFromController1.getLogin(), user2.getLogin());
        assertEquals(userFromController1.getName(), user2.getName());
        assertEquals(userFromController1.getBirthday(), user2.getBirthday());

        assertEquals(userFromController2.getId(), user3.getId());
        assertEquals(userFromController2.getEmail(), user3.getEmail());
        assertEquals(userFromController2.getLogin(), user3.getLogin());
        assertEquals(userFromController2.getName(), user3.getName());
        assertEquals(userFromController2.getBirthday(), user3.getBirthday());
    }

    @Test
    public void shouldGetEmptyUserFriends() {
        User user1 = User.builder()
                .id(1)
                .email("tester1@yandex.ru")
                .name("Test name 1")
                .login("ValidTestLogin1")
                .birthday(LocalDate.of(1964, 6, 11))
                .build();
        userController.createUser(user1);

        List<User> friends = userController.getUserFriends(1);

        assertEquals(friends.size(), 0);
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
        userController.createUser(user1);

        User user2 = User.builder()
                .id(2)
                .email("tester2@yandex.ru")
                .name("Test name 2")
                .login("ValidTestLogin2")
                .birthday(LocalDate.of(1984, 9, 4))
                .build();
        userController.createUser(user2);

        User user3 = User.builder()
                .id(3)
                .email("tester3@yandex.ru")
                .name("Test name 3")
                .login("ValidTestLogin3")
                .birthday(LocalDate.of(1984, 9, 4))
                .build();
        userController.createUser(user3);

        userController.addFriend(2, 1);
        userController.addFriend(3, 1);
        List<User> commonFriends12 = userController.getUserCommonFriends(1, 2);
        List<User> commonFriends13 = userController.getUserCommonFriends(1, 3);
        List<User> commonFriends23 = userController.getUserCommonFriends(2, 3);

        assertEquals(commonFriends12.size(), 0);
        assertEquals(commonFriends13.size(), 0);
        assertEquals(commonFriends23.size(), 1);

        User userFromController = commonFriends23.get(0);

        assertEquals(userFromController.getId(), user1.getId());
        assertEquals(userFromController.getEmail(), user1.getEmail());
        assertEquals(userFromController.getLogin(), user1.getLogin());
        assertEquals(userFromController.getName(), user1.getName());
        assertEquals(userFromController.getBirthday(), user1.getBirthday());
    }

    @Test
    public void shouldGetEmptyUserCommonFriends() {
        User user1 = User.builder()
                .id(1)
                .email("tester1@yandex.ru")
                .name("Test name 1")
                .login("ValidTestLogin1")
                .birthday(LocalDate.of(1964, 6, 11))
                .build();
        userController.createUser(user1);

        User user2 = User.builder()
                .id(2)
                .email("tester2@yandex.ru")
                .name("Test name 2")
                .login("ValidTestLogin2")
                .birthday(LocalDate.of(1984, 9, 4))
                .build();
        userController.createUser(user2);

        User user3 = User.builder()
                .id(3)
                .email("tester3@yandex.ru")
                .name("Test name 3")
                .login("ValidTestLogin3")
                .birthday(LocalDate.of(1984, 9, 4))
                .build();
        userController.createUser(user3);

        userController.addFriend(1, 2);
        userController.addFriend(2, 3);
        userController.addFriend(3, 1);
        List<User> commonFriends12 = userController.getUserCommonFriends(1, 2);
        List<User> commonFriends13 = userController.getUserCommonFriends(1, 3);
        List<User> commonFriends23 = userController.getUserCommonFriends(2, 3);

        assertEquals(commonFriends12.size(), 0);
        assertEquals(commonFriends13.size(), 0);
        assertEquals(commonFriends23.size(), 0);
    }
}
