package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotLikeFilmException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    public static final LocalDate FIRST_FILM_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    private void validate(Film film) {
        if (film.getReleaseDate().isBefore(FIRST_FILM_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза фильма не может быть раньше " + FIRST_FILM_RELEASE_DATE);
        }
    }

    public Film createFilm(Film film) {
        log.info("Добавление фильма {}", film);
        validate(film);
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        log.info("Обновление фильма {}", film);
        validate(film);
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(int filmId) {
        log.info("Вывод фильма с id {}", filmId);
        return filmStorage.getFilm(filmId);
    }

    public Collection<Film> getAllFilms() {
        log.info("Вывод всех фильмов.");
        return filmStorage.getAllFilms().values();
    }

    public Boolean addLike(int filmId, int userId) {
        log.info("Добавляем лайк пользователя с id {} фильму с id {}.", userId, filmId);
        Film film = filmStorage.getFilm(filmId);
        film.getLikes().add(userId);
        return true;
    }

    public Boolean removeLike(int filmId, int userId) {
        log.info("Удаляем лайк пользователя с id {} фильму с id {}.", userId, filmId);
        User user = userStorage.getUser(userId);
        Film film = filmStorage.getFilm(filmId);
        if (!film.getLikes().contains(userId)) {
            throw new UserNotLikeFilmException("Пользователь не поставил лайк фильму.");
        }
        film.getLikes().remove(userId);
        return true;
    }

    public List<Film> getTopRatedFilms(int count) {
        return filmStorage.getAllFilms().values().stream()
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}
