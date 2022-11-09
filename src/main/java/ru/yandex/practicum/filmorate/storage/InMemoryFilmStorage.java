package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage{
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;

    @Override
    public Film createFilm(Film film) {
        if (films.containsKey(film.getId())) {
            String message = "Фильм с таким id уже существует.";
            log.error(message);
            throw new FilmAlreadyExistException(message);
        }
        film.setId(id++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            String message = "Фильма с таким id не существует.";
            log.error(message);
            throw new FilmNotFoundException(message);
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Boolean deleteFilm(int filmId) {
        if (!films.containsKey(filmId)) {
            String message = "Фильма с таким id не существует.";
            log.error(message);
            throw new FilmNotFoundException(message);
        }
        films.remove(filmId);
        return true;
    }

    @Override
    public Film getFilm(int filmId) {
        if (!films.containsKey(filmId)) {
            String message = "Фильма с таким id не существует.";
            log.error(message);
            throw new FilmNotFoundException(message);
        }
        return films.get(filmId);
    }

    @Override
    public Map<Integer, Film> getAllFilms() {
        return films;
    }
}
