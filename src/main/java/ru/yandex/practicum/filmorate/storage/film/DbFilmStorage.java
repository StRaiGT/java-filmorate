package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

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
    private final UserStorage dbUserStorage;
    private final DirectorStorage dbDirectorStorage;

    @Override
    public Film createFilm(Film film) {
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
                    preparedStatement.setInt(5, film.getMpa()
                            .getId());
                    return preparedStatement;
                },
                keyHolder
        );
        film.setId(Objects.requireNonNull(keyHolder.getKey())
                .intValue());
        addFilmGenres(film);
        addFilmDirectors(film);
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
                film.getMpa()
                        .getId(),
                film.getId()
        );
        removeFilmGenres(film);
        addFilmGenres(film);
        removeFilmDirectors(film);
        addFilmDirectors(film);
        return getFilm(film.getId());
    }

    @Override
    public Film getFilm(int filmId) {
        final String sqlQuery = "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, " +
                "m.MPA_ID, m.NAME, m.DESCRIPTION, " +
                "g.GENRE_ID, g.NAME, " +
                "d.DIRECTOR_ID, d.NAME " +
                "FROM FILMS AS f " +
                "JOIN MPA AS m ON f.MPA_ID = m.MPA_ID " +
                "LEFT JOIN FILMS_GENRES AS fg ON f.FILM_ID = fg.FILM_ID " +
                "LEFT JOIN GENRES AS g ON fg.GENRE_ID = g.GENRE_ID " +
                "LEFT JOIN FILMS_DIRECTORS AS fd ON f.FILM_ID = fd.FILM_ID " +
                "LEFT JOIN DIRECTORS AS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                "WHERE f.film_id = ?";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilms, filmId);
        if (films.size() == 0) {
            throw new NotFoundException("Фильма с таким id не существует.");
        }
        return films.get(0);
    }

    @Override
    public Boolean deleteFilm(int id) {
        final String sqlQuery = "DELETE FROM FILMS " +
                "WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
        return true;
    }

    @Override
    public List<Film> getAllFilms() {
        final String sqlQuery = "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, " +
                "m.MPA_ID, m.NAME, m.DESCRIPTION, " +
                "g.GENRE_ID, g.NAME, " +
                "d.DIRECTOR_ID, d.NAME " +
                "FROM FILMS AS f " +
                "JOIN MPA AS m ON f.MPA_ID = m.MPA_ID " +
                "LEFT JOIN FILMS_GENRES AS fg ON f.FILM_ID = fg.FILM_ID " +
                "LEFT JOIN GENRES AS g ON fg.GENRE_ID = g.GENRE_ID " +
                "LEFT JOIN FILMS_DIRECTORS AS fd ON f.FILM_ID = fd.FILM_ID " +
                "LEFT JOIN DIRECTORS AS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID";
        return jdbcTemplate.query(sqlQuery, this::makeFilms);
    }

    private void addFilmGenres(Film film) {
        try {
            if (film.getGenres() != null) {
                final String sqlQuery = "INSERT INTO FILMS_GENRES " +
                        "VALUES (?, ?)";
                List<Object[]> batch = new ArrayList<>();
                film.getGenres()
                        .stream()
                        .map(Genre::getId)
                        .distinct()
                        .forEach(genreId -> batch.add(new Object[]{film.getId(), genreId}));
                jdbcTemplate.batchUpdate(sqlQuery, batch);
            }
        } catch (DataIntegrityViolationException exception) {
            throw new NotFoundException("Фильма с таким id не существует.");
        }
    }

    private void removeFilmGenres(Film film) {
        final String sqlQuery = "DELETE FROM FILMS_GENRES " +
                "WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
    }

    private void addFilmDirectors(Film film) {
        try {
            if (film.getDirectors() != null) {
                final String sqlQuery = "INSERT INTO FILMS_DIRECTORS " +
                        "VALUES (?, ?)";
                List<Object[]> batch = new ArrayList<>();
                film.getDirectors()
                        .stream()
                        .map(Director::getId)
                        .distinct()
                        .forEach(directorId -> batch.add(new Object[]{film.getId(), directorId}));
                jdbcTemplate.batchUpdate(sqlQuery, batch);
            }
        } catch (DataIntegrityViolationException exception) {
            throw new NotFoundException("Фильма с таким id не существует.");
        }
    }

    private void removeFilmDirectors(Film film) {
        final String sqlQuery = "DELETE FROM FILMS_DIRECTORS " +
                "WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
    }

    @Override
    public Boolean addLike(int filmId, int userId) {
        getFilm(filmId);
        dbUserStorage.getUser(userId);
        final String sqlQuery = "MERGE INTO LIKES (FILM_ID, USER_ID) " +
                "VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
        return true;
    }

    @Override
    public Boolean removeLike(int filmId, int userId) {
        getFilm(filmId);
        dbUserStorage.getUser(userId);
        final String sqlQuery = "DELETE FROM LIKES " +
                "WHERE FILM_ID = ?" +
                "AND USER_ID = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
        return true;
    }

    @Override
    public List<Film> getTopRatedFilms(int count) {
        final String sqlQuery = "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, " +
                "m.MPA_ID, m.NAME, m.DESCRIPTION, " +
                "g.GENRE_ID, g.NAME, " +
                "d.DIRECTOR_ID, d.NAME " +
                "FROM FILMS AS f " +
                "JOIN MPA AS m ON f.MPA_ID = m.MPA_ID " +
                "LEFT JOIN FILMS_GENRES AS fg ON f.FILM_ID = fg.FILM_ID " +
                "LEFT JOIN GENRES AS g ON fg.GENRE_ID = g.GENRE_ID " +
                "LEFT JOIN FILMS_DIRECTORS AS fd ON f.FILM_ID = fd.FILM_ID " +
                "LEFT JOIN DIRECTORS AS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                "LEFT JOIN (" +
                "SELECT l.FILM_ID, COUNT(l.USER_ID) AS rate " +
                "FROM LIKES AS l " +
                "GROUP BY l.FILM_ID" +
                ") AS r ON f.FILM_ID = r.FILM_ID " +
                "ORDER BY r.rate DESC";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilms);
        List<Film> result = new ArrayList<>();
        films.stream()
                .limit(count)
                .forEach(result::add);
        return result;
    }

    private List<Film> makeFilms(ResultSet resultSet) throws SQLException {
        List<Integer> orderId = new ArrayList<>();
        Map<Integer, Film> films = new HashMap<>();
        while (resultSet.next()) {
            int id = resultSet.getInt("FILM_ID");
            orderId.add(id);
            films.putIfAbsent(id, Film.builder()
                    .id(resultSet.getInt("FILM_ID"))
                    .name(resultSet.getString("FILMS.NAME"))
                    .description(resultSet.getString("FILMS.DESCRIPTION"))
                    .releaseDate(resultSet.getDate("RELEASE_DATE")
                            .toLocalDate())
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
                films.get(id)
                        .getGenres()
                        .add(genre);
            }
            Director director = Director.builder()
                    .id(resultSet.getInt("DIRECTOR_ID"))
                    .name(resultSet.getString("DIRECTORS.NAME"))
                    .build();
            if (director.getId() != 0) {
                films.get(id)
                        .getDirectors()
                        .add(director);
            }
        }
        List<Film> orderResult = new ArrayList<>();
        orderId.stream()
                .distinct()
                .forEach((id) -> orderResult.add(films.get(id)));
        return orderResult;
    }

    @Override
    public List<Film> getFilmsByDirectorSortLikes(int id) {
        dbDirectorStorage.getDirector(id);
        final String sqlQuery = "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, " +
                "m.MPA_ID, m.NAME, m.DESCRIPTION, " +
                "g.GENRE_ID, g.NAME, " +
                "d.DIRECTOR_ID, d.NAME " +
                "FROM FILMS AS f " +
                "JOIN MPA AS m ON f.MPA_ID = m.MPA_ID " +
                "LEFT JOIN FILMS_GENRES AS fg ON f.FILM_ID = fg.FILM_ID " +
                "LEFT JOIN GENRES AS g ON fg.GENRE_ID = g.GENRE_ID " +
                "LEFT JOIN FILMS_DIRECTORS AS fd ON f.FILM_ID = fd.FILM_ID " +
                "LEFT JOIN DIRECTORS AS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                "LEFT JOIN (" +
                "SELECT l.FILM_ID, COUNT(l.USER_ID) AS rate " +
                "FROM LIKES AS l " +
                "GROUP BY l.FILM_ID" +
                ") AS r ON f.FILM_ID = r.FILM_ID " +
                "WHERE fd.DIRECTOR_ID = ? " +
                "ORDER BY r.rate DESC";
        return jdbcTemplate.query(sqlQuery, this::makeFilms, id);
    }

    @Override
    public List<Film> getFilmsByDirectorSortYear(int id) {
        dbDirectorStorage.getDirector(id);
        final String sqlQuery = "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, " +
                "m.MPA_ID, m.NAME, m.DESCRIPTION, " +
                "g.GENRE_ID, g.NAME, " +
                "d.DIRECTOR_ID, d.NAME " +
                "FROM FILMS AS f " +
                "JOIN MPA AS m ON f.MPA_ID = m.MPA_ID " +
                "LEFT JOIN FILMS_GENRES AS fg ON f.FILM_ID = fg.FILM_ID " +
                "LEFT JOIN GENRES AS g ON fg.GENRE_ID = g.GENRE_ID " +
                "LEFT JOIN FILMS_DIRECTORS AS fd ON f.FILM_ID = fd.FILM_ID " +
                "LEFT JOIN DIRECTORS AS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                "WHERE fd.DIRECTOR_ID = ? " +
                "ORDER BY EXTRACT(YEAR FROM f.RELEASE_DATE)";
        return jdbcTemplate.query(sqlQuery, this::makeFilms, id);
    }

    @Override
    public List<Film> receiveFilmRecommendations(int userId) {
        final String maxIntersectionByLikesUserQuery = "SELECT user_id, COUNT(film_id) AS c " +
                "FROM likes " +
                "WHERE film_id IN (" +
                "SELECT film_id " +
                "FROM likes " +
                "WHERE user_id = ?) " +
                "AND user_id != ? " +
                "GROUP BY user_id " +
                "ORDER BY c DESC " +
                "LIMIT 1;";

        final String recommendedFilmsQuery = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, " +
                "m.mpa_id, m.name, m.description, " +
                "g.genre_id, g.name, " +
                "d.director_id, d.name " +
                "FROM films AS f " +
                "JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN films_genres AS fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genres AS g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN films_directors AS fd ON f.film_id = fd.film_id " +
                "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                "LEFT JOIN likes AS l ON f.film_id = l.film_id " +
                "WHERE f.film_id NOT IN (" +
                "SELECT film_id " +
                "FROM likes " +
                "WHERE user_id = ?) " +
                "AND l.user_id = ?";

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(maxIntersectionByLikesUserQuery, userId, userId);

        if (!rowSet.next()) {
            return new ArrayList<>();
        }
        return jdbcTemplate.query(recommendedFilmsQuery, this::makeFilms, userId, rowSet.getInt("user_id"));
    }

    @Override
    public List<Film> searchFilmsByDirector(String query) {
        final String sqlByDirector = "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, " +
                "m.MPA_ID, m.NAME, m.DESCRIPTION, " +
                "g.GENRE_ID, g.NAME, " +
                "d.DIRECTOR_ID, d.NAME " +
                "FROM FILMS AS f " +
                "JOIN MPA AS m ON f.MPA_ID = m.MPA_ID " +
                "LEFT JOIN FILMS_GENRES AS fg ON f.FILM_ID = fg.FILM_ID " +
                "LEFT JOIN GENRES AS g ON fg.GENRE_ID = g.GENRE_ID " +
                "LEFT JOIN FILMS_DIRECTORS AS fd ON f.FILM_ID = fd.FILM_ID " +
                "LEFT JOIN DIRECTORS AS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                "WHERE LOWER(d.name) like ? ";

        return jdbcTemplate.query(sqlByDirector, this::makeFilms, query);
    }

    @Override
    public List<Film> searchFilmsByTitle(String query) {
        final String sqlByFilm = "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, " +
                "m.MPA_ID, m.NAME, m.DESCRIPTION, " +
                "g.GENRE_ID, g.NAME, " +
                "d.DIRECTOR_ID, d.NAME " +
                "FROM FILMS AS f " +
                "JOIN MPA AS m ON f.MPA_ID = m.MPA_ID " +
                "LEFT JOIN FILMS_GENRES AS fg ON f.FILM_ID = fg.FILM_ID " +
                "LEFT JOIN GENRES AS g ON fg.GENRE_ID = g.GENRE_ID " +
                "LEFT JOIN FILMS_DIRECTORS AS fd ON f.FILM_ID = fd.FILM_ID " +
                "LEFT JOIN DIRECTORS AS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                "WHERE LOWER(f.name) like ?";
        return jdbcTemplate.query(sqlByFilm, this::makeFilms, query);
    }

    @Override
    public List<Film> searchFilmsByDirectorOrFilm(String filmName, String directorName) {
        final String sqlByFilmOrDirector = "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, " +
                "m.MPA_ID, m.NAME, m.DESCRIPTION, " +
                "g.GENRE_ID, g.NAME, " +
                "d.DIRECTOR_ID, d.NAME " +
                "FROM FILMS AS f " +
                "JOIN MPA AS m ON f.MPA_ID = m.MPA_ID " +
                "LEFT JOIN FILMS_GENRES AS fg ON f.FILM_ID = fg.FILM_ID " +
                "LEFT JOIN GENRES AS g ON fg.GENRE_ID = g.GENRE_ID " +
                "LEFT JOIN FILMS_DIRECTORS AS fd ON f.FILM_ID = fd.FILM_ID " +
                "LEFT JOIN DIRECTORS AS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                "LEFT JOIN LIKES l on f.film_id = l.film_id " +
                "WHERE LOWER(f.name) LIKE ? OR " +
                "LOWER(d.name) LIKE ? " +
                "GROUP BY f.film_id, g.GENRE_ID, d.DIRECTOR_ID " +
                "ORDER BY COUNT(l.film_id) DESC";
        return jdbcTemplate.query(sqlByFilmOrDirector, this::makeFilms, filmName, directorName);
    }
}
