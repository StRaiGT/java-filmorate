package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    @Override
    public User createUser(User user) {
        if (users.containsKey(user.getId())) {
            String message = "Пользователь с таким id уже существует.";
            log.error(message);
            throw new UserAlreadyExistException(message);
        }
        user.setId(id++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            String message = "Пользователя с таким id не существует.";
            log.error(message);
            throw new UserNotFoundException(message);
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Boolean deleteUser(int userId) {
        if (!users.containsKey(userId)) {
            String message = "Пользователя с таким id не существует.";
            log.error(message);
            throw new UserNotFoundException(message);
        }
        users.remove(userId);
        return true;
    }

    @Override
    public User getUser(int userId) {
        if (!users.containsKey(userId)) {
            String message = "Пользователя с таким id не существует.";
            log.error(message);
            throw new UserNotFoundException(message);
        }
        return users.get(userId);
    }

    @Override
    public Map<Integer, User> getAllUsers() {
        return users;
    }
}
