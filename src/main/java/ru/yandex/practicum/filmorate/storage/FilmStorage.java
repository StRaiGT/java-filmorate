package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;

public interface FilmStorage {
    Film createFilm (Film film);
    Film updateFilm(Film film);
    Boolean deleteFilm(int filmId);
    Film getFilm(int filmId);
    Map<Integer, Film> getAllFilms();
}
