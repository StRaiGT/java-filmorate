package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.List;

import static ru.yandex.practicum.filmorate.enums.EventType.LIKE;
import static ru.yandex.practicum.filmorate.enums.Operation.ADD;
import static ru.yandex.practicum.filmorate.enums.Operation.REMOVE;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final FeedService feedService;
    private final UserService userService;

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

    public Boolean deleteFilm(int id) {
        log.info("Удаление фильма с id {}", id);
        return filmStorage.deleteFilm(id);
    }

    public List<Film> getAllFilms() {
        log.info("Вывод всех фильмов.");
        return filmStorage.getAllFilms();
    }

    public Boolean addLike(int filmId, int userId) {
        log.info("Добавляем лайк пользователя с id {} фильму с id {}.", userId, filmId);
        boolean like = filmStorage.addLike(filmId, userId);
        feedService.add(filmId, userId, LIKE, ADD);
        return like;
    }

    public Boolean removeLike(int filmId, int userId) {
        log.info("Удаляем лайк пользователя с id {} фильму с id {}.", userId, filmId);
        boolean like = filmStorage.removeLike(filmId, userId);
        feedService.add(filmId, userId, LIKE, REMOVE);
        return like;
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

    public List<Film> searchFilms(String query, String by) {
        log.info("Возвращаем результат поиска фильмов" +
                " по запросу {} или по названию фильма или имени режиссёра {}.", query, by);

        query = "%" + query.toLowerCase() + "%";

        String[] byList = by.split(",");

        if (byList.length != 0) {
            if (byList.length == 1) {
                if (byList[0].equals("director")) {
                    return filmStorage.searchFilmsByDirector(query);
                } else if (byList[0].equals("title")) {
                    return filmStorage.searchFilmsByTitle(query);
                }
            } else if ((byList[0].equals("director") && byList[1].equals("title")) ||
                    (byList[0].equals("title") && byList[1].equals("director")) &&
                            byList.length == 2) {
                return filmStorage.searchFilmsByDirectorOrFilm(query, query);
            }
        }
        throw new ValidationException("Некорректные параметры запроса!");
    }
}
