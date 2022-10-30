package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController extends Controller<Film> {
    @GetMapping
    public Collection<Film> getAll() {
        return items.values();
    }

    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        log.info("Добавление фильма {}", film);
        validate(film);
        film.setId(id++);
        items.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Обновление фильма {}", film);
        if (!items.containsKey(film.getId())) {
            String message = "Фильма с таким id не существует.";
            log.error(message);
            throw new ValidationException(message);
        }
        validate(film);
        items.put(film.getId(), film);
        return film;
    }

    protected void validate(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            String message = "Дата релиза фильма не может быть раньше 28 декабря 1985 года.";
            log.error(message);
            throw new ValidationException(message);
        }
    }
}
