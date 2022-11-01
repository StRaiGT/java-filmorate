package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;
    static final LocalDate FIRST_FILM_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    static final int MAX_DESCRIPTION_LENGTH = 200;

    @GetMapping
    public Collection<Film> getAll() {
        return films.values();
    }

    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        log.info("Добавление фильма {}", film);
        if (films.containsKey(film.getId())) {
            String message = "Фильм с таким id уже существует.";
            log.error(message);
            throw new ValidationException(message);
        }
        validate(film);
        film.setId(id++);
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Обновление фильма {}", film);
        if (!films.containsKey(film.getId())) {
            String message = "Фильма с таким id не существует.";
            log.error(message);
            throw new ValidationException(message);
        }
        validate(film);
        films.put(film.getId(), film);
        return film;
    }

    private void validate(Film film) {
        if (film.getName().isBlank()) {
            String message = "Название фильма не может быть пустым";
            log.error(message);
            throw new ValidationException(message);
        }
        if (film.getDescription().length() > 200) {
            String message = "Максимальная длина описания — " + MAX_DESCRIPTION_LENGTH + " символов";
            log.error(message);
            throw new ValidationException(message);
        }
        if (film.getReleaseDate().isBefore(FIRST_FILM_RELEASE_DATE)) {
            String message = "Дата релиза фильма не может быть раньше " + FIRST_FILM_RELEASE_DATE;
            log.error(message);
            throw new ValidationException(message);
        }
        if (film.getDuration() <= 0) {
            String message = "Продолжительность фильма должна быть положительной";
            log.error(message);
            throw new ValidationException(message);
        }
    }
}
