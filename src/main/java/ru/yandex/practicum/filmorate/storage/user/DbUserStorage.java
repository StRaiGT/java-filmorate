package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Component
public class DbUserStorage implements UserStorage{
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbUserStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User createUser(User user) {
        try {
            final String sqlQuery = "INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY) " +
                    "VALUES (?, ?, ?, ?);";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sqlQuery, new String[]{"USER_ID"});
                ps.setString(1, user.getEmail());
                ps.setString(2, user.getLogin());
                ps.setString(3, user.getName());
                ps.setDate(4, Date.valueOf(user.getBirthday()));
                return ps;
            }, keyHolder);
            user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
            return user;
        } catch (DuplicateKeyException e) {
            throw new AlreadyExistException("Пользователь с таким login уже существует.");
        }
    }

    @Override
    public User updateUser(User user) {
        getUser(user.getId());
        try {
            final String sqlQuery = "UPDATE USERS " +
                    "SET EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? " +
                    "WHERE USER_ID = ?";
            jdbcTemplate.update(
                    sqlQuery,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday(),
                    user.getId()
            );
            return user;
        } catch (DuplicateKeyException e) {
            throw new AlreadyExistException("Пользователь с таким login уже существует.");
        }
    }

    @Override
    public User getUser(int userId) {
        try {
            final String sqlQuery = "SELECT * " +
                "FROM USERS " +
                "WHERE USER_ID = ?";
            return jdbcTemplate.queryForObject(sqlQuery, DbUserStorage::makeUser, userId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователя с таким id не существует.");
        }
    }

    @Override
    public List<User> getAllUsers() {
        final String sqlQuery = "SELECT * " +
                "FROM USERS";
        return jdbcTemplate.query(sqlQuery, DbUserStorage::makeUser);
    }

    @Override
    public Boolean addFriend(int userId, int friendId) {
        try {
            final String sqlQuery = "INSERT INTO FRIENDSHIP (USER_ID, FRIEND_ID) " +
                    "VALUES (?, ?)";
            jdbcTemplate.update(
                    sqlQuery,
                    userId,
                    friendId
            );
            return true;
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("Пользователя с таким id не существует.");
        }
    }

    @Override
    public Boolean removeFriend(int userId, int friendId) {
        final String sqlQuery = "DELETE FROM FRIENDSHIP " +
                "WHERE USER_ID = ? " +
                "AND FRIEND_ID = ?";
        jdbcTemplate.update(
                sqlQuery,
                userId,
                friendId);
        return true;
    }

    @Override
    public List<User> getUserFriends(int userId) {
        final String sqlQuery = "SELECT * " +
                "FROM USERS " +
                "WHERE USER_ID IN (" +
                    "SELECT FRIEND_ID " +
                    "FROM FRIENDSHIP " +
                    "WHERE USER_ID = ?" +
                ")";
        return jdbcTemplate.query(sqlQuery, DbUserStorage::makeUser, userId);
    }

    public List<User> getUserCommonFriends(int userId, int friendId) {
        final String sqlQuery = "SELECT * " +
                "FROM USERS " +
                "where USER_ID IN (" +
                    "SELECT FRIEND_ID " +
                    "FROM FRIENDSHIP " +
                    "where USER_ID = ?) " +
                "AND USER_ID IN (" +
                    "SELECT FRIEND_ID " +
                    "FROM FRIENDSHIP " +
                    "where USER_ID = ?)";
        return jdbcTemplate.query(sqlQuery, DbUserStorage::makeUser, userId, friendId);
    }

    public static User makeUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("USER_ID"))
                .email(resultSet.getString("EMAIL"))
                .login(resultSet.getString("LOGIN"))
                .name(resultSet.getString("NAME"))
                .birthday(resultSet.getDate("BIRTHDAY").toLocalDate())
                .build();
    }
}
