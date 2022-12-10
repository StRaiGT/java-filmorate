package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class DbMpaStorage implements MpaStorage{
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbMpaStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getAllMpa() {
        final String sqlQuery = "SELECT * " +
                "FROM MPA";
        return jdbcTemplate.query(sqlQuery, DbMpaStorage::makeMpa);
    }

    @Override
    public Mpa getMpaById(int id) {
        try {
            final String sqlQuery = "SELECT * " +
                    "FROM MPA " +
                    "WHERE MPA_ID = ?";
            return jdbcTemplate.queryForObject(sqlQuery, DbMpaStorage::makeMpa, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Рейтинга MPA с таким id не существует.");
        }
    }

    public static Mpa makeMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("MPA_ID"))
                .name(resultSet.getString("NAME"))
                .description(resultSet.getString("DESCRIPTION"))
                .build();
    }
}
