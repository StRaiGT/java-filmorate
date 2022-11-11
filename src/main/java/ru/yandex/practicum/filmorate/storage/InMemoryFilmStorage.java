package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage{
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;

    @Override
    public Film createFilm(Film film) {
        if (films.containsKey(film.getId())) {
            throw new FilmAlreadyExistException("Фильм с таким id уже существует.");
        }
        film.setId(id++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new FilmNotFoundException("Фильма с таким id не существует.");
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Boolean deleteFilm(int filmId) {
        if (!films.containsKey(filmId)) {
            throw new FilmNotFoundException("Фильма с таким id не существует.");
        }
        films.remove(filmId);
        return true;
    }

    @Override
    public Film getFilm(int filmId) {
        if (!films.containsKey(filmId)) {
            throw new FilmNotFoundException("Фильма с таким id не существует.");
        }
        return films.get(filmId);
    }

    @Override
    public Map<Integer, Film> getAllFilms() {
        return films;
    }
}
