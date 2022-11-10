package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IllegalAddFriendException;
import ru.yandex.practicum.filmorate.exception.UsersNotFriendsException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    private void validate(User user) {
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Неправильный формат логина.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("Имя пользователя не передано, вместо имени установлен переданный логин.");
            user.setName(user.getLogin());
        }
    }

    public User createUser(User user) {
        log.info("Добавление пользователя {}", user);
        validate(user);
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        log.info("Обновление пользователя {}", user);
        validate(user);
        return userStorage.updateUser(user);
    }

    public User getUserById(int userId) {
        log.info("Вывод пользователя с id {}", userId);
        return userStorage.getUser(userId);
    }

    public Collection<User> getAllUsers() {
        log.info("Вывод всех пользователей.");
        return userStorage.getAllUsers().values();
    }

    public Boolean addFriend(int userId, int friendId) {
        log.info("Добавляем в друзья пользователей с id {} и {}.", userId, friendId);
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        if (userId == friendId) {
            throw new IllegalAddFriendException("Пользователь не может добавить в друзья себя самого.");
        }

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        return true;
    }

    public Boolean removeFriend(int userId, int friendId) {
        log.info("Удаляем из друзей пользователей с id {} и {}.", userId, friendId);
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        if (!user.getFriends().contains(friendId) || !friend.getFriends().contains(userId)) {
            throw new UsersNotFriendsException("Пользователи не являются друзьями.");
        }

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        return true;
    }

    public List<User> getUserFriends(int userId) {
        log.info("Выводим друзей пользователя с id {}.", userId);
        return userStorage.getUser(userId).getFriends().stream()
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }

    public List<User> getUserCommonFriends(int userId, int friendId) {
        log.info("Выводим общих друзей пользователей с id {} и {}.", userId, friendId);
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        return user.getFriends().stream()
                .filter(u -> friend.getFriends().contains(u))
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }
}
