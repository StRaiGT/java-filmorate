package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User createUser(User user);
    User updateUser(User user);
    User getUser(int userId);
    List<User> getAllUsers();
    Boolean addFriend(int userId, int friendId);
    Boolean removeFriend(int userId, int friendId);
    List<User> getUserFriends(int userId);
    List<User> getUserCommonFriends(int userId, int friendId);
}
