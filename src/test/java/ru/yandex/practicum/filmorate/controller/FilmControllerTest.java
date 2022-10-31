package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTest {
    private FilmController controller;

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
        ArrayList<Film> arr = new ArrayList<>(controller.getAll());

        assertEquals(arr.get(0).getName(), "test film name");
        assertEquals(arr.get(0).getDescription(), "description");
        assertEquals(arr.get(0).getDuration(), 100);
        assertEquals(arr.get(0).getReleaseDate(), LocalDate.of(1967, 3, 25));
        assertEquals(arr.get(0).getId(), 1);
    }

    private static Stream<Arguments> films() {
        String badDescription = "";
        for (int i = 0; i < 210; ++i) {
            badDescription += "/";
        }
        return Stream.of(
                Arguments.of(Film.builder()
                                .name("")
                                .description("description")
                                .duration(100)
                                .releaseDate(LocalDate.of(1967, 3, 25))
                                .build()),
                Arguments.of(Film.builder()
                                .name("test film name")
                                .description(badDescription)
                                .duration(100)
                                .releaseDate(LocalDate.of(1967, 3, 25))
                                .build()),
                Arguments.of(Film.builder()
                                .name("test film name")
                                .description("description")
                                .duration(-200)
                                .releaseDate(LocalDate.of(1967, 3, 25))
                                .build()));
    }

    @ParameterizedTest
    @MethodSource("films")
    public void shouldNotAddFilmWithNotValidFields(Film film) {
        assertEquals(controller.getAll().size(), 0);
    }

    @Test
    public void shouldThrowsExceptionIfNotValidDate() {
        Film film = Film.builder()
                .name("test film name")
                .description("description")
                .duration(100)
                .releaseDate(LocalDate.of(1600, 6, 1))
                .build();

        ValidationException exception = assertThrows(ValidationException.class, () -> {controller.add(film);});
        assertEquals("Дата релиза фильма не может быть раньше 28 декабря 1985 года.", exception.getMessage());
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

        Film newFilm = Film.builder()
                .id(1)
                .name("update film name")
                .description("update description")
                .duration(200)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .build();
        controller.update(newFilm);

        ArrayList<Film> arr = new ArrayList<>(controller.getAll());

        assertEquals(arr.get(0).getName(), "update film name");
        assertEquals(arr.get(0).getDescription(), "update description");
        assertEquals(arr.get(0).getDuration(), 200);
        assertEquals(arr.get(0).getReleaseDate(), LocalDate.of(2000, 1, 1));
        assertEquals(arr.get(0).getId(), 1);
    }

    @Test
    public void shouldThrowExceptionIfFilmIdNotFound() {
        Film film = Film.builder()
                .name("test film name")
                .description("description")
                .duration(100)
                .releaseDate(LocalDate.of(1967, 3, 25))
                .build();
        controller.add(film);

        Film newFilm = Film.builder()
                .id(999)
                .name("update film name")
                .description("update description")
                .duration(200)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .build();

        ValidationException exception = assertThrows(ValidationException.class, () -> {controller.update(newFilm);});
        assertEquals("Фильма с таким id не существует.", exception.getMessage());
    }
}
