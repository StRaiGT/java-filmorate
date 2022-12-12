package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film createFilm (Film film);
    Film updateFilm(Film film);
    Film getFilm(int filmId);
    List<Film> getAllFilms();
    Boolean addLike(int filmId, int userId);
    Boolean removeLike(int filmId, int userId);
    List<Film> getTopRatedFilms(int count);
}