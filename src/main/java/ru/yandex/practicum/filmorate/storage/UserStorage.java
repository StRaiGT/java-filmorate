package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;

public interface UserStorage {
    User createUser(User user);
    User updateUser(User user);
    Boolean deleteUser(int userId);
    User getUser(int userId);
    Map<Integer, User> getAllUsers();
}
