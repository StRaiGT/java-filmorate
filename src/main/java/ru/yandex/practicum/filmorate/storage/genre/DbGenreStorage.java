package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class DbGenreStorage implements GenreStorage{
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbGenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getAllGenres() {
        final String sqlQuery = "SELECT * " +
                "FROM GENRES " +
                "ORDER BY GENRE_ID";
        return jdbcTemplate.query(sqlQuery, DbGenreStorage::makeGenre);
    }

    @Override
    public Genre getGenreById(int id) {
        try {
            final String sqlQuery = "SELECT * " +
                    "FROM GENRES " +
                    "WHERE GENRE_ID = ?";
            return jdbcTemplate.queryForObject(sqlQuery, DbGenreStorage::makeGenre, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Жанра с таким id не существует.");
        }
    }

    public static Genre makeGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("GENRE_ID"))
                .name(resultSet.getString("NAME"))
                .build();
    }
}
