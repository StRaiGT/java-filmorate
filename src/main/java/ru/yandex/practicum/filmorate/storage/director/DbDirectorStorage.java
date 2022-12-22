package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class DbDirectorStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Director createDirector(Director director) {
        try {
            final String sqlQuery = "INSERT INTO DIRECTORS (NAME) " +
                        "VALUES (?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(
                    connection -> {
                        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, new String[]{"DIRECTOR_ID"});
                        preparedStatement.setString(1, director.getName());
                        return preparedStatement;
                    },
                    keyHolder
            );
            director.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
            return director;
        } catch (DuplicateKeyException e) {
            throw new AlreadyExistException("Режиссер с таким именем уже существует.");
        }
    }

    @Override
    public Director updateDirector(Director director) {
        try {
            final String sqlQuery = "UPDATE DIRECTORS " +
                    "SET NAME = ? " +
                    "WHERE DIRECTOR_ID = ?";
            jdbcTemplate.update(sqlQuery, director.getName(), director.getId());
            return getDirector(director.getId());
        } catch (DuplicateKeyException e) {
            throw new AlreadyExistException("Режиссер с таким именем уже существует.");
        }
    }

    @Override
    public Boolean deleteDirector(int id) {
        final String sqlQuery = "DELETE " +
                "FROM DIRECTORS " +
                "WHERE DIRECTOR_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
        return true;
    }

    @Override
    public Director getDirector(int id) {
        try {
            final String sqlQuery = "SELECT * " +
                    "FROM DIRECTORS " +
                    "WHERE DIRECTOR_ID = ?";
            return jdbcTemplate.queryForObject(sqlQuery, this::makeDirector, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Режиссера с таким id не существует.");
        }
    }

    @Override
    public List<Director> getAllDirectors() {
        final String sqlQuery = "SELECT * " +
                "FROM DIRECTORS";
        return jdbcTemplate.query(sqlQuery, this::makeDirector);
    }

    private Director makeDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return Director.builder()
                .id(resultSet.getInt("DIRECTOR_ID"))
                .name(resultSet.getString("NAME"))
                .build();
    }
}
