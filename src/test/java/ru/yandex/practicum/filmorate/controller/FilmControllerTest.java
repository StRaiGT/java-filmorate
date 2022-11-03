package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTest {
    private static FilmController controller;

    @BeforeEach
    public void createController() {
        controller = new FilmController();
    }

    @Test
    public void shouldAddFilmWithValidFields() throws ValidationException{
        Film film = Film.builder()
                .name("test film name")
                .description("description")
                .duration(100)
                .releaseDate(LocalDate.of(1967, 3, 25))
                .build();
        controller.add(film);
        List<Film> arr = new ArrayList<>(controller.getAll());

        assertEquals(arr.size(), 1);

        Film filmFromController = arr.get(0);

        assertEquals(filmFromController.getName(), film.getName());
        assertEquals(filmFromController.getDescription(), film.getDescription());
        assertEquals(filmFromController.getDuration(), film.getDuration());
        assertEquals(filmFromController.getReleaseDate(), film.getReleaseDate());
        assertEquals(filmFromController.getId(), 1);
    }

    @Test
    public void shouldThrowExceptionIfAddFilmIdFound() {
        Film film = Film.builder()
                .name("test film name")
                .description("description")
                .duration(100)
                .releaseDate(LocalDate.of(1967, 3, 25))
                .build();
        controller.add(film);

        Film newFilm = Film.builder()
                .id(1)
                .name("test film name")
                .description("description")
                .duration(100)
                .releaseDate(LocalDate.of(1967, 3, 25))
                .build();

        ValidationException exception = assertThrows(ValidationException.class, () -> {controller.add(newFilm);});
        assertEquals("Фильм с таким id уже существует.", exception.getMessage());
        assertEquals(controller.getAll().size(), 1);
    }

    @Test
    public void shouldThrowExceptionIfFilmWithNotValidReleaseDate() {
        Film film = Film.builder()
                .name("test film name")
                .description("description")
                .duration(100)
                .releaseDate(LocalDate.of(1600, 6, 1))
                .build();
        ValidationException exception = assertThrows(ValidationException.class, () -> {controller.add(film);});
        assertEquals("Дата релиза фильма не может быть раньше " + controller.FIRST_FILM_RELEASE_DATE,
                exception.getMessage());
        assertEquals(controller.getAll().size(), 0);
    }

    @Test
    public void shouldUpdateFilm() {
        Film film = Film.builder()
                .name("test film name")
                .description("description")
                .duration(100)
                .releaseDate(LocalDate.of(1967, 3, 25))
                .build();
        controller.add(film);

        Film updatedFilm = Film.builder()
                .id(1)
                .name("update film name")
                .description("update description")
                .duration(200)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .build();
        controller.update(updatedFilm);

        List<Film> arr = new ArrayList<>(controller.getAll());

        assertEquals(arr.size(), 1);

        Film filmFromController = arr.get(0);

        assertEquals(filmFromController.getName(), updatedFilm.getName());
        assertEquals(filmFromController.getDescription(), updatedFilm.getDescription());
        assertEquals(filmFromController.getDuration(), updatedFilm.getDuration());
        assertEquals(filmFromController.getReleaseDate(), updatedFilm.getReleaseDate());
        assertEquals(filmFromController.getId(), updatedFilm.getId());
    }

    @Test
    public void shouldThrowExceptionIfUpdateFilmIdNotFound() {
        Film film = Film.builder()
                .name("test film name")
                .description("description")
                .duration(100)
                .releaseDate(LocalDate.of(1967, 3, 25))
                .build();
        controller.add(film);

        Film updatedFilm = Film.builder()
                .id(999)
                .name("update film name")
                .description("update description")
                .duration(200)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .build();

        ValidationException exception = assertThrows(ValidationException.class, () -> {controller.update(updatedFilm);});
        assertEquals("Фильма с таким id не существует.", exception.getMessage());
        assertEquals(controller.getAll().size(), 1);
    }
}
