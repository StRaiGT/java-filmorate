package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DbMpaStorage implements MpaStorage{
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> getAllMpa() {
        final String sqlQuery = "SELECT * " +
                "FROM MPA";
        return jdbcTemplate.query(sqlQuery, this::makeMpa);
    }

    @Override
    public Mpa getMpaById(int id) {
        try {
            final String sqlQuery = "SELECT * " +
                    "FROM MPA " +
                    "WHERE MPA_ID = ?";
            return jdbcTemplate.queryForObject(sqlQuery, this::makeMpa, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Рейтинга MPA с таким id не существует.");
        }
    }

    private Mpa makeMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("MPA_ID"))
                .name(resultSet.getString("NAME"))
                .description(resultSet.getString("DESCRIPTION"))
                .build();
    }
}
