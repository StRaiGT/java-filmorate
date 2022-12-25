package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.List;

import static ru.yandex.practicum.filmorate.storage.feed.EventType.LIKE;
import static ru.yandex.practicum.filmorate.storage.feed.Operation.ADD;
import static ru.yandex.practicum.filmorate.storage.feed.Operation.REMOVE;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final FeedService feedService;

    public static final LocalDate FIRST_FILM_RELEASE_DATE = LocalDate.of(1895, 12, 28);

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

    public List<Film> getAllFilms() {
        log.info("Вывод всех фильмов.");
        return filmStorage.getAllFilms();
    }

    public Boolean addLike(int filmId, int userId) {
        log.info("Добавляем лайк пользователя с id {} фильму с id {}.", userId, filmId);
        feedService.add(filmId, userId, LIKE, ADD);
        return filmStorage.addLike(filmId, userId);
    }

    public Boolean removeLike(int filmId, int userId) {
        log.info("Удаляем лайк пользователя с id {} фильму с id {}.", userId, filmId);
        feedService.add(filmId, userId, LIKE, REMOVE);
        return filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getTopRatedFilms(int count) {
        log.info("Возвращаем топ {} фильмов.", count);
        return filmStorage.getTopRatedFilms(count);
    }

    public List<Film> getFilmsByDirector(int directorId, String sortBy) {
        log.info("Возвращаем фильмы режиссера с id {}.", directorId);
        List<Film> films;
        if (sortBy.equals("likes")) {
            films = filmStorage.getFilmsByDirectorSortLikes(directorId);
        } else if (sortBy.equals("year")) {
            films = filmStorage.getFilmsByDirectorSortYear(directorId);
        } else {
            throw new ValidationException(String.format("Некорректные параметры сортировки в запросе."));
        }
        return films;
    }
}
