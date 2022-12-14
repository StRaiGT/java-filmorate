package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IllegalAddFriendException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

import static ru.yandex.practicum.filmorate.enums.EventType.FRIEND;
import static ru.yandex.practicum.filmorate.enums.Operation.ADD;
import static ru.yandex.practicum.filmorate.enums.Operation.REMOVE;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final FeedService feedService;

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

    public Boolean deleteUser(int id) {
        log.info("Удаление пользователя с id {}", id);
        return userStorage.deleteUser(id);
    }

    public List<User> getAllUsers() {
        log.info("Вывод всех пользователей.");
        return userStorage.getAllUsers();
    }

    public Boolean addFriend(int userId, int friendId) {
        log.info("Добавляем в друзья пользователей с id {} и {}.", userId, friendId);
        if (userId == friendId) {
            throw new IllegalAddFriendException("Пользователь не может добавить в друзья себя самого.");
        }
        getUserById(userId);
        getUserById(friendId);
        feedService.add(friendId, userId, FRIEND, ADD);
        return userStorage.addFriend(userId, friendId);
    }

    public Boolean removeFriend(int userId, int friendId) {
        log.info("Удаляем из друзей пользователей с id {} и {}.", userId, friendId);
        getUserById(userId);
        getUserById(friendId);
        feedService.add(friendId, userId, FRIEND, REMOVE);
        return userStorage.removeFriend(userId, friendId);
    }

    public List<User> getUserFriends(int userId) {
        log.info("Выводим друзей пользователя с id {}.", userId);
        return userStorage.getUserFriends(userId);
    }

    public List<User> getUserCommonFriends(int userId, int friendId) {
        log.info("Выводим общих друзей пользователей с id {} и {}.", userId, friendId);
        return userStorage.getUserCommonFriends(userId, friendId);
    }

    public List<Film> receiveFilmRecommendations(int userId) {
        log.info("Выводим рекомендации фильмов для пользователя с id {} ", userId);
        return filmStorage.receiveFilmRecommendations(userId);
    }

    public List<Feed> getFeedByUserId(Integer id) {
        getUserById(id);
        return feedService.getByUserId(id);
    }
}
