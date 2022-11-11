package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    @Override
    public User createUser(User user) {
        if (users.containsKey(user.getId())) {
            throw new UserAlreadyExistException("Пользователь с таким id уже существует.");
        }
        user.setId(id++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException("Пользователя с таким id не существует.");
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Boolean deleteUser(int userId) {
        if (!users.containsKey(userId)) {
            throw new UserNotFoundException("Пользователя с таким id не существует.");
        }
        users.remove(userId);
        return true;
    }

    @Override
    public User getUser(int userId) {
        if (!users.containsKey(userId)) {
            throw new UserNotFoundException("Пользователя с таким id не существует.");
        }
        return users.get(userId);
    }

    @Override
    public Map<Integer, User> getAllUsers() {
        return users;
    }
}
