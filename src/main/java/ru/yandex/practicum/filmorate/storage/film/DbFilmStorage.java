package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.user.DbUserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class DbFilmStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final DbUserStorage dbUserStorage;

    @Override
    public Film createFilm(Film film) {
        try {
            final String sqlQuery = "INSERT INTO FILMS (NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID) " +
                    "VALUES (?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(
                    connection -> {
                        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, new String[]{"FILM_ID"});
                        preparedStatement.setString(1, film.getName());
                        preparedStatement.setString(2, film.getDescription());
                        preparedStatement.setDate(3, Date.valueOf(film.getReleaseDate()));
                        preparedStatement.setInt(4, film.getDuration());
                        preparedStatement.setInt(5, film.getMpa().getId());
                        return preparedStatement;
                        },
                    keyHolder
            );
            film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        } catch (DuplicateKeyException e) {
            throw new AlreadyExistException("Фильм с таким названием уже существует.");
        }
        addFilmGenres(film);
        return getFilm(film.getId());
    }

    @Override
    public Film updateFilm(Film film) {
        final String sqlQuery = "UPDATE FILMS " +
                "SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA_ID = ? " +
                "WHERE FILM_ID = ?";
        jdbcTemplate.update(
                sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        removeFilmGenres(film);
        addFilmGenres(film);
        return getFilm(film.getId());
    }

    @Override
    public Film getFilm(int filmId) {
        final String sqlQuery = "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, " +
                "m.MPA_ID, m.NAME, m.DESCRIPTION, " +
                "g.GENRE_ID, g.NAME " +
                "FROM FILMS AS f " +
                "JOIN MPA AS m ON f.MPA_ID = m.MPA_ID " +
                "LEFT JOIN FILMS_GENRES AS fg ON f.FILM_ID = fg.FILM_ID " +
                "LEFT JOIN GENRES AS g ON fg.GENRE_ID = g.GENRE_ID " +
                "WHERE f.film_id = ?";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilms, filmId);

        if (films.size() == 0) {
            throw new NotFoundException("Фильма с таким id не существует.");
        }
        return films.get(0);
    }

    @Override
    public Film deleteById(int id) {
        Film film = getFilm(id);
      //  final String genresSqlQuery = "DELETE FROM FILMS_GENRES WHERE FILM_ID = ?";
       // String mpaSqlQuery = "DELETE FROM FILMS WHERE MPA_ID = ?";

       // jdbcTemplate.update(genresSqlQuery, id);
        //jdbcTemplate.update(mpaSqlQuery, id);
        final String sqlQuery = "DELETE FROM films WHERE FILM_ID = ?";

        jdbcTemplate.update(sqlQuery, id);
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        final String sqlQuery = "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, " +
                "m.MPA_ID, m.NAME, m.DESCRIPTION, " +
                "g.GENRE_ID, g.NAME " +
                "FROM FILMS AS f " +
                "JOIN MPA AS m ON f.MPA_ID = m.MPA_ID " +
                "LEFT JOIN FILMS_GENRES AS fg ON f.FILM_ID = fg.FILM_ID " +
                "LEFT JOIN GENRES AS g ON fg.GENRE_ID = g.GENRE_ID";
        return jdbcTemplate.query(sqlQuery, this::makeFilms);
    }

    private void addFilmGenres(Film film) {
        if (film.getGenres() != null) {
            final String sqlQuery = "INSERT INTO FILMS_GENRES " +
                    "VALUES (?, ?)";
            List<Object[]> batch = new ArrayList<>();
            film.getGenres().stream()
                    .map(Genre::getId)
                    .distinct()
                    .forEach(genreId -> batch.add(new Object[]{film.getId(), genreId}));
            jdbcTemplate.batchUpdate(sqlQuery, batch);
        }
    }

    private void removeFilmGenres(Film film) {
        final String sqlQuery = "DELETE FROM FILMS_GENRES " +
                "WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
    }

    public Boolean addLike(int filmId, int userId) {
        getFilm(filmId);
        dbUserStorage.getUser(userId);

        final String sqlQuery = "MERGE INTO LIKES (FILM_ID, USER_ID) " +
                "VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
        return true;
    }

    public Boolean removeLike(int filmId, int userId) {
        getFilm(filmId);
        dbUserStorage.getUser(userId);

        final String sqlQuery = "DELETE FROM LIKES " +
                "WHERE FILM_ID = ?" +
                "AND USER_ID = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
        return true;
    }

    public List<Film> getTopRatedFilms(int count) {
        final String sqlQuery = "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, " +
                "m.MPA_ID, m.NAME, m.DESCRIPTION, " +
                "g.GENRE_ID, g.NAME " +
                "FROM FILMS AS f " +
                "JOIN MPA AS m ON f.MPA_ID = m.MPA_ID " +
                "LEFT JOIN FILMS_GENRES AS fg ON f.FILM_ID = fg.FILM_ID " +
                "LEFT JOIN GENRES AS g ON fg.GENRE_ID = g.GENRE_ID " +
                "LEFT JOIN (" +
                    "SELECT l.FILM_ID, COUNT(l.USER_ID) AS rate " +
                    "FROM LIKES AS l " +
                    "GROUP BY l.FILM_ID" +
                ") AS r ON f.FILM_ID = r.FILM_ID " +
                "ORDER BY r.rate DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::makeFilms, count);
    }

    private List<Film> makeFilms(ResultSet resultSet) throws SQLException {
        Map<Integer, Film> films = new HashMap<>();
        while (resultSet.next()) {
            int id = resultSet.getInt("FILM_ID");

            films.putIfAbsent(id, Film.builder()
                    .id(resultSet.getInt("FILM_ID"))
                    .name(resultSet.getString("FILMS.NAME"))
                    .description(resultSet.getString("FILMS.DESCRIPTION"))
                    .releaseDate(resultSet.getDate("RELEASE_DATE").toLocalDate())
                    .duration(resultSet.getInt("DURATION"))
                    .mpa(Mpa.builder()
                            .id(resultSet.getInt("MPA_ID"))
                            .name(resultSet.getString("MPA.NAME"))
                            .description(resultSet.getString("MPA.DESCRIPTION"))
                            .build())
                    .build()
            );

            Genre genre = Genre.builder()
                    .id(resultSet.getInt("GENRE_ID"))
                    .name(resultSet.getString("GENRES.NAME"))
                    .build();
            if (genre.getId() != 0) {
                films.get(id).getGenres().add(genre);
            }
        }
        return new ArrayList<>(films.values());
    }
}
