package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FilmControllerTest {
    private final FilmController filmController;
    private final UserStorage userStorage;

    @Test
    public void shouldAddFilmWithValidFields() {
        Film film = Film.builder()
                .name("test film name")
                .description("description")
                .duration(100)
                .releaseDate(LocalDate.of(1967, 3, 25))
                .mpa(Mpa.builder().id(1).build())
                .build();
        film.getGenres().add(Genre.builder()
                .id(1)
                .build());
        filmController.createFilm(film);

        List<Film> arr = new ArrayList<>(filmController.getAllFilms());

        assertEquals(arr.size(), 1);

        Film filmFromController = arr.get(0);

        assertEquals(filmFromController.getId(), 1);
        assertEquals(filmFromController.getName(), film.getName());
        assertEquals(filmFromController.getDescription(), film.getDescription());
        assertEquals(filmFromController.getDuration(), film.getDuration());
        assertEquals(filmFromController.getReleaseDate(), film.getReleaseDate());
        assertEquals(filmFromController.getMpa().getId(), film.getMpa().getId());
        assertEquals(filmFromController.getGenres().size(), film.getGenres().size());
        assertEquals(new ArrayList<>(filmFromController.getGenres()).get(0).getId(),
                new ArrayList<>(film.getGenres()).get(0).getId());
    }

    @Test
    public void shouldThrowExceptionIfAddFilmNameFound() {
        Film film = Film.builder()
                .name("test film name")
                .description("description")
                .duration(100)
                .releaseDate(LocalDate.of(1967, 3, 25))
                .mpa(Mpa.builder().id(1).build())
                .build();
        film.getGenres().add(Genre.builder()
                .id(1)
                .build());
        filmController.createFilm(film);

        Film newFilm = Film.builder()
                .name("test film name")
                .description("description 2")
                .duration(200)
                .releaseDate(LocalDate.of(1987, 3, 25))
                .mpa(Mpa.builder().id(2).build())
                .build();
        film.getGenres().add(Genre.builder()
                .id(2)
                .build());

        AlreadyExistException exception = assertThrows(AlreadyExistException.class, () -> filmController.createFilm(newFilm));
        assertEquals("Фильм с таким названием уже существует.", exception.getMessage());
        assertEquals(filmController.getAllFilms().size(), 1);
    }

    @Test
    public void shouldThrowExceptionIfFilmWithNotValidReleaseDate() {
        Film film = Film.builder()
                .name("test film name")
                .description("description")
                .duration(100)
                .releaseDate(LocalDate.of(1600, 6, 1))
                .mpa(Mpa.builder().id(1).build())
                .build();
        film.getGenres().add(Genre.builder()
                .id(1)
                .build());

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.createFilm(film));
        assertEquals("Дата релиза фильма не может быть раньше " + FilmService.FIRST_FILM_RELEASE_DATE,
                exception.getMessage());
        assertEquals(filmController.getAllFilms().size(), 0);
    }

    @Test
    public void shouldNoDuplicateGenres()  {
        Film film = Film.builder()
                .name("test film name")
                .description("description")
                .duration(100)
                .releaseDate(LocalDate.of(1967, 3, 25))
                .mpa(Mpa.builder().id(1).build())
                .build();
        film.getGenres().add(Genre.builder()
                .id(1)
                .build());
        film.getGenres().add(Genre.builder()
                .id(2)
                .build());
        film.getGenres().add(Genre.builder()
                .id(1)
                .build());
        filmController.createFilm(film);

        Film filmFromController = filmController.getFilmById(1);

        assertEquals(filmFromController.getName(), film.getName());
        assertEquals(filmFromController.getDescription(), film.getDescription());
        assertEquals(filmFromController.getDuration(), film.getDuration());
        assertEquals(filmFromController.getReleaseDate(), film.getReleaseDate());
        assertEquals(filmFromController.getMpa().getId(), film.getMpa().getId());
        assertEquals(filmFromController.getGenres().size(), 2);
        assertEquals(new ArrayList<>(filmFromController.getGenres()).get(0).getId(), 1);
        assertEquals(new ArrayList<>(filmFromController.getGenres()).get(1).getId(), 2);
    }

    @Test
    public void shouldUpdateFilm() {
        Film film = Film.builder()
                .id(1)
                .name("test film name")
                .description("description")
                .duration(100)
                .releaseDate(LocalDate.of(1967, 3, 25))
                .mpa(Mpa.builder().id(1).build())
                .build();
        film.getGenres().add(Genre.builder()
                .id(1)
                .build());
        filmController.createFilm(film);

        Film updatedFilm = Film.builder()
                .id(1)
                .name("update film name")
                .description("update description")
                .duration(200)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .mpa(Mpa.builder().id(2).build())
                .build();
        updatedFilm.getGenres().add(Genre.builder()
                .id(2)
                .build());
        updatedFilm.getGenres().add(Genre.builder()
                .id(3)
                .build());
        filmController.updateFilm(updatedFilm);

        List<Film> arr = new ArrayList<>(filmController.getAllFilms());

        assertEquals(arr.size(), 1);

        Film filmFromController = arr.get(0);

        assertEquals(filmFromController.getId(), updatedFilm.getId());
        assertEquals(filmFromController.getName(), updatedFilm.getName());
        assertEquals(filmFromController.getDescription(), updatedFilm.getDescription());
        assertEquals(filmFromController.getDuration(), updatedFilm.getDuration());
        assertEquals(filmFromController.getReleaseDate(), updatedFilm.getReleaseDate());
        assertEquals(filmFromController.getMpa().getId(), updatedFilm.getMpa().getId());
        assertEquals(filmFromController.getGenres().size(), updatedFilm.getGenres().size());
        assertEquals(new ArrayList<>(filmFromController.getGenres()).get(0).getId(),
                new ArrayList<>(updatedFilm.getGenres()).get(0).getId());
        assertEquals(new ArrayList<>(filmFromController.getGenres()).get(1).getId(),
                new ArrayList<>(updatedFilm.getGenres()).get(1).getId());
    }

    @Test
    public void shouldThrowExceptionIfUpdateFilmIdNotFound() {
        Film film = Film.builder()
                .id(1)
                .name("test film name")
                .description("description")
                .duration(100)
                .releaseDate(LocalDate.of(1967, 3, 25))
                .mpa(Mpa.builder().id(1).build())
                .build();
        film.getGenres().add(Genre.builder()
                .id(1)
                .build());
        filmController.createFilm(film);

        Film updatedFilm = Film.builder()
                .id(999)
                .name("update film name")
                .description("update description")
                .duration(200)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .mpa(Mpa.builder().id(2).build())
                .build();

        NotFoundException exception = assertThrows(NotFoundException.class, () -> filmController.updateFilm(updatedFilm));
        assertEquals("Фильма с таким id не существует.", exception.getMessage());

        List<Film> arr = new ArrayList<>(filmController.getAllFilms());

        assertEquals(arr.size(), 1);

        Film filmFromController = arr.get(0);

        assertEquals(filmFromController.getId(), film.getId());
        assertEquals(filmFromController.getName(), film.getName());
        assertEquals(filmFromController.getDescription(), film.getDescription());
        assertEquals(filmFromController.getDuration(), film.getDuration());
        assertEquals(filmFromController.getReleaseDate(), film.getReleaseDate());
        assertEquals(filmFromController.getMpa().getId(), film.getMpa().getId());
        assertEquals(filmFromController.getGenres().size(), film.getGenres().size());
        assertEquals(new ArrayList<>(filmFromController.getGenres()).get(0).getId(),
                new ArrayList<>(film.getGenres()).get(0).getId());
    }

    @Test
    public void shouldGetAllFilms() {
        Film film1 = Film.builder()
                .id(1)
                .name("test film name 1")
                .description("description 1")
                .duration(100)
                .releaseDate(LocalDate.of(1967, 3, 25))
                .mpa(Mpa.builder().id(1).build())
                .build();
        film1.getGenres().add(Genre.builder()
                .id(1)
                .build());
        filmController.createFilm(film1);

        Film film2 = Film.builder()
                .id(2)
                .name("test film name 2")
                .description("description 2")
                .duration(100)
                .releaseDate(LocalDate.of(1987, 8, 5))
                .mpa(Mpa.builder().id(1).build())
                .build();
        film2.getGenres().add(Genre.builder()
                .id(2)
                .build());
        film2.getGenres().add(Genre.builder()
                .id(3)
                .build());
        filmController.createFilm(film2);
        List<Film> arr = filmController.getAllFilms();

        assertEquals(arr.size(), 2);

        Film filmFromController1 = arr.get(0);
        Film filmFromController2 = arr.get(1);

        assertEquals(filmFromController1.getId(), film1.getId());
        assertEquals(filmFromController1.getName(), film1.getName());
        assertEquals(filmFromController1.getDescription(), film1.getDescription());
        assertEquals(filmFromController1.getDuration(), film1.getDuration());
        assertEquals(filmFromController1.getReleaseDate(), film1.getReleaseDate());
        assertEquals(filmFromController1.getMpa().getId(), film1.getMpa().getId());
        assertEquals(filmFromController1.getGenres().size(), film1.getGenres().size());
        assertEquals(new ArrayList<>(filmFromController1.getGenres()).get(0).getId(),
                new ArrayList<>(film1.getGenres()).get(0).getId());

        assertEquals(filmFromController2.getId(), film2.getId());
        assertEquals(filmFromController2.getName(), film2.getName());
        assertEquals(filmFromController2.getDescription(), film2.getDescription());
        assertEquals(filmFromController2.getDuration(), film2.getDuration());
        assertEquals(filmFromController2.getReleaseDate(), film2.getReleaseDate());
        assertEquals(filmFromController2.getMpa().getId(), film2.getMpa().getId());
        assertEquals(filmFromController2.getGenres().size(), film2.getGenres().size());
        assertEquals(new ArrayList<>(filmFromController2.getGenres()).get(0).getId(),
                new ArrayList<>(film2.getGenres()).get(0).getId());
        assertEquals(new ArrayList<>(filmFromController2.getGenres()).get(1).getId(),
                new ArrayList<>(film2.getGenres()).get(1).getId());
    }

    @Test
    public void shouldGetEmptyIfNoFilms() {
        List<Film> arr = new ArrayList<>(filmController.getAllFilms());

        assertEquals(arr.size(), 0);
    }

    @Test
    public void shouldGetFilmById() {
        Film film = Film.builder()
                .id(1)
                .name("test film name")
                .description("description")
                .duration(100)
                .releaseDate(LocalDate.of(1967, 3, 25))
                .mpa(Mpa.builder().id(1).build())
                .build();
        film.getGenres().add(Genre.builder()
                .id(1)
                .build());
        filmController.createFilm(film);
        Film filmFromController = filmController.getFilmById(1);

        assertEquals(filmFromController.getId(), film.getId());
        assertEquals(filmFromController.getName(), film.getName());
        assertEquals(filmFromController.getDescription(), film.getDescription());
        assertEquals(filmFromController.getDuration(), film.getDuration());
        assertEquals(filmFromController.getReleaseDate(), film.getReleaseDate());
        assertEquals(filmFromController.getMpa().getId(), film.getMpa().getId());
        assertEquals(filmFromController.getGenres().size(), film.getGenres().size());
        assertEquals(new ArrayList<>(filmFromController.getGenres()).get(0).getId(),
                new ArrayList<>(film.getGenres()).get(0).getId());
    }

    @Test
    public void shouldThrowExceptionIfFilmIdNotFound() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> filmController.getFilmById(999));
        assertEquals("Фильма с таким id не существует.", exception.getMessage());
    }

    @Test
    public void shouldAddLike() {
        Film film1 = Film.builder()
                .id(1)
                .name("test film name")
                .description("description")
                .duration(100)
                .releaseDate(LocalDate.of(1967, 3, 25))
                .mpa(Mpa.builder().id(1).build())
                .build();
        film1.getGenres().add(Genre.builder()
                .id(1)
                .build());
        filmController.createFilm(film1);

        Film film2 = Film.builder()
                .id(2)
                .name("test film name 2")
                .description("description")
                .duration(100)
                .releaseDate(LocalDate.of(1967, 3, 25))
                .mpa(Mpa.builder().id(1).build())
                .build();
        film2.getGenres().add(Genre.builder()
                .id(2)
                .build());
        filmController.createFilm(film2);

        Film film3 = Film.builder()
                .id(3)
                .name("test film name 3")
                .description("description")
                .duration(100)
                .releaseDate(LocalDate.of(1967, 3, 25))
                .mpa(Mpa.builder().id(1).build())
                .build();
        film3.getGenres().add(Genre.builder()
                .id(3)
                .build());
        filmController.createFilm(film3);

        User user = User.builder()
                .id(1)
                .email("tester@yandex.ru")
                .name("Test name")
                .login("ValidTestLogin")
                .birthday(LocalDate.of(1964, 6, 11))
                .build();
        userStorage.createUser(user);

        assertTrue(filmController.addLike(2, 1));

        List<Film> arr = filmController.getTopRatedFilms(1);

        assertEquals(arr.size(), 1);

        Film filmFromController1 = arr.get(0);

        assertEquals(filmFromController1.getId(), film2.getId());
        assertEquals(filmFromController1.getName(), film2.getName());
        assertEquals(filmFromController1.getDescription(), film2.getDescription());
        assertEquals(filmFromController1.getDuration(), film2.getDuration());
        assertEquals(filmFromController1.getReleaseDate(), film2.getReleaseDate());
        assertEquals(filmFromController1.getMpa().getId(), film2.getMpa().getId());
        assertEquals(filmFromController1.getGenres().size(), film2.getGenres().size());
        assertEquals(new ArrayList<>(filmFromController1.getGenres()).get(0).getId(),
                new ArrayList<>(film2.getGenres()).get(0).getId());
    }

    @Test
    public void shouldThrowExceptionIfLikeNotFoundFilm() {
        User user = User.builder()
                .id(1)
                .email("tester@yandex.ru")
                .name("Test name")
                .login("ValidTestLogin")
                .birthday(LocalDate.of(1964, 6, 11))
                .build();
        userStorage.createUser(user);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> filmController.addLike(1, 1));
        assertEquals("Фильма с таким id не существует.", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionIfLikeNotFoundUser() {
        Film film = Film.builder()
                .id(1)
                .name("test film name")
                .description("description")
                .duration(100)
                .releaseDate(LocalDate.of(1967, 3, 25))
                .mpa(Mpa.builder().id(1).build())
                .build();
        film.getGenres().add(Genre.builder()
                .id(1)
                .build());
        filmController.createFilm(film);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> filmController.addLike(1, 1));
        assertEquals("Пользователя с таким id не существует.", exception.getMessage());
    }

    @Test
    public void shouldRemoveLike() {
        Film film1 = Film.builder()
                .id(1)
                .name("test film name")
                .description("description")
                .duration(100)
                .releaseDate(LocalDate.of(1967, 3, 25))
                .mpa(Mpa.builder().id(1).build())
                .build();
        film1.getGenres().add(Genre.builder()
                .id(1)
                .build());
        filmController.createFilm(film1);

        Film film2 = Film.builder()
                .id(2)
                .name("test film name 2")
                .description("description")
                .duration(100)
                .releaseDate(LocalDate.of(1967, 3, 25))
                .mpa(Mpa.builder().id(1).build())
                .build();
        film2.getGenres().add(Genre.builder()
                .id(2)
                .build());
        filmController.createFilm(film2);

        Film film3 = Film.builder()
                .id(3)
                .name("test film name 3")
                .description("description")
                .duration(100)
                .releaseDate(LocalDate.of(1967, 3, 25))
                .mpa(Mpa.builder().id(1).build())
                .build();
        film3.getGenres().add(Genre.builder()
                .id(3)
                .build());
        filmController.createFilm(film3);

        User user = User.builder()
                .id(1)
                .email("tester@yandex.ru")
                .name("Test name")
                .login("ValidTestLogin")
                .birthday(LocalDate.of(1964, 6, 11))
                .build();
        userStorage.createUser(user);

        assertTrue(filmController.addLike(2, 1));

        List<Film> arr1 = filmController.getTopRatedFilms(1);

        assertEquals(arr1.size(), 1);

        Film filmFromController1 = arr1.get(0);

        assertEquals(filmFromController1.getId(), film2.getId());
        assertEquals(filmFromController1.getName(), film2.getName());
        assertEquals(filmFromController1.getDescription(), film2.getDescription());
        assertEquals(filmFromController1.getDuration(), film2.getDuration());
        assertEquals(filmFromController1.getReleaseDate(), film2.getReleaseDate());
        assertEquals(filmFromController1.getMpa().getId(), film2.getMpa().getId());
        assertEquals(filmFromController1.getGenres().size(), film2.getGenres().size());
        assertEquals(new ArrayList<>(filmFromController1.getGenres()).get(0).getId(),
                new ArrayList<>(film2.getGenres()).get(0).getId());

        assertTrue(filmController.removeLike(2, 1));

        List<Film> arr2 = filmController.getTopRatedFilms(1);

        assertEquals(arr2.size(), 1);

        Film filmFromController2 = arr2.get(0);

        assertEquals(filmFromController2.getId(), film1.getId());
        assertEquals(filmFromController2.getName(), film1.getName());
        assertEquals(filmFromController2.getDescription(), film1.getDescription());
        assertEquals(filmFromController2.getDuration(), film1.getDuration());
        assertEquals(filmFromController2.getReleaseDate(), film1.getReleaseDate());
        assertEquals(filmFromController2.getMpa().getId(), film1.getMpa().getId());
        assertEquals(filmFromController2.getGenres().size(), film1.getGenres().size());
        assertEquals(new ArrayList<>(filmFromController2.getGenres()).get(0).getId(),
                new ArrayList<>(film1.getGenres()).get(0).getId());
    }

    @Test
    public void shouldThrowExceptionIfDelLikeNotFoundFilm() {
        User user = User.builder()
                .id(1)
                .email("tester@yandex.ru")
                .name("Test name")
                .login("ValidTestLogin")
                .birthday(LocalDate.of(1964, 6, 11))
                .build();
        userStorage.createUser(user);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> filmController.removeLike(1, 1));
        assertEquals("Фильма с таким id не существует.", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionIfDelLikeNotFoundUser() {
        Film film = Film.builder()
                .id(1)
                .name("test film name")
                .description("description")
                .duration(100)
                .releaseDate(LocalDate.of(1967, 3, 25))
                .mpa(Mpa.builder().id(1).build())
                .build();
        film.getGenres().add(Genre.builder()
                .id(1)
                .build());
        filmController.createFilm(film);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> filmController.removeLike(1, 1));
        assertEquals("Пользователя с таким id не существует.", exception.getMessage());
    }

    @Test
    public void shouldGetTwoTopRatedFilms() {
        Film film1 = Film.builder()
                .id(1)
                .name("test film name 1")
                .description("description")
                .duration(100)
                .releaseDate(LocalDate.of(1967, 3, 25))
                .mpa(Mpa.builder().id(1).build())
                .build();
        film1.getGenres().add(Genre.builder()
                .id(1)
                .build());
        filmController.createFilm(film1);

        Film film2 = Film.builder()
                .id(2)
                .name("test film name 2")
                .description("description")
                .duration(100)
                .releaseDate(LocalDate.of(1967, 3, 25))
                .mpa(Mpa.builder().id(1).build())
                .build();
        film2.getGenres().add(Genre.builder()
                .id(2)
                .build());
        filmController.createFilm(film2);

        Film film3 = Film.builder()
                .id(3)
                .name("test film name 3")
                .description("description")
                .duration(100)
                .releaseDate(LocalDate.of(1967, 3, 25))
                .mpa(Mpa.builder().id(1).build())
                .build();
        film3.getGenres().add(Genre.builder()
                .id(3)
                .build());
        filmController.createFilm(film3);

        User user1 = User.builder()
                .id(1)
                .email("tester1@yandex.ru")
                .name("Test name 1")
                .login("ValidTestLogin1")
                .birthday(LocalDate.of(1964, 6, 11))
                .build();
        userStorage.createUser(user1);

        User user2 = User.builder()
                .id(2)
                .email("tester2@yandex.ru")
                .name("Test name 2")
                .login("ValidTestLogin2")
                .birthday(LocalDate.of(1964, 6, 11))
                .build();
        userStorage.createUser(user2);

        filmController.addLike(2, 1);
        filmController.addLike(3, 1);
        filmController.addLike(3, 2);
        List<Film> arr = filmController.getTopRatedFilms(2);

        assertEquals(arr.size(), 2);

        Film filmFromController1 = arr.get(0);
        Film filmFromController2 = arr.get(1);

        assertEquals(filmFromController1.getId(), film3.getId());
        assertEquals(filmFromController1.getName(), film3.getName());
        assertEquals(filmFromController1.getDescription(), film3.getDescription());
        assertEquals(filmFromController1.getDuration(), film3.getDuration());
        assertEquals(filmFromController1.getReleaseDate(), film3.getReleaseDate());
        assertEquals(filmFromController1.getMpa().getId(), film3.getMpa().getId());
        assertEquals(filmFromController1.getGenres().size(), film3.getGenres().size());
        assertEquals(new ArrayList<>(filmFromController1.getGenres()).get(0).getId(),
                new ArrayList<>(film3.getGenres()).get(0).getId());

        assertEquals(filmFromController2.getId(), film2.getId());
        assertEquals(filmFromController2.getName(), film2.getName());
        assertEquals(filmFromController2.getDescription(), film2.getDescription());
        assertEquals(filmFromController2.getDuration(), film2.getDuration());
        assertEquals(filmFromController2.getReleaseDate(), film2.getReleaseDate());
        assertEquals(filmFromController2.getMpa().getId(), film2.getMpa().getId());
        assertEquals(filmFromController2.getGenres().size(), film2.getGenres().size());
        assertEquals(new ArrayList<>(filmFromController2.getGenres()).get(0).getId(),
                new ArrayList<>(film2.getGenres()).get(0).getId());
    }

    @Test
    public void shouldGetMostTopRateFilm() {
        Film film1 = Film.builder()
                .id(1)
                .name("test film name 1")
                .description("description")
                .duration(100)
                .releaseDate(LocalDate.of(1967, 3, 25))
                .mpa(Mpa.builder().id(1).build())
                .build();
        film1.getGenres().add(Genre.builder()
                .id(1)
                .build());
        filmController.createFilm(film1);

        Film film2 = Film.builder()
                .id(2)
                .name("test film name 2")
                .description("description")
                .duration(100)
                .releaseDate(LocalDate.of(1967, 3, 25))
                .mpa(Mpa.builder().id(1).build())
                .build();
        film2.getGenres().add(Genre.builder()
                .id(2)
                .build());
        filmController.createFilm(film2);

        Film film3 = Film.builder()
                .id(3)
                .name("test film name 3")
                .description("description")
                .duration(100)
                .releaseDate(LocalDate.of(1967, 3, 25))
                .mpa(Mpa.builder().id(1).build())
                .build();
        film3.getGenres().add(Genre.builder()
                .id(3)
                .build());
        filmController.createFilm(film3);

        User user1 = User.builder()
                .id(1)
                .email("tester1@yandex.ru")
                .name("Test name 1")
                .login("ValidTestLogin1")
                .birthday(LocalDate.of(1964, 6, 11))
                .build();
        userStorage.createUser(user1);

        User user2 = User.builder()
                .id(2)
                .email("tester2@yandex.ru")
                .name("Test name 2")
                .login("ValidTestLogin2")
                .birthday(LocalDate.of(1964, 6, 11))
                .build();
        userStorage.createUser(user2);

        filmController.addLike(2, 1);
        filmController.addLike(2, 1);
        filmController.addLike(3, 2);
        List<Film> arr = filmController.getTopRatedFilms(1);

        assertEquals(arr.size(), 1);

        Film filmFromController1 = arr.get(0);

        assertEquals(filmFromController1.getId(), film2.getId());
        assertEquals(filmFromController1.getName(), film2.getName());
        assertEquals(filmFromController1.getDescription(), film2.getDescription());
        assertEquals(filmFromController1.getDuration(), film2.getDuration());
        assertEquals(filmFromController1.getReleaseDate(), film2.getReleaseDate());
        assertEquals(filmFromController1.getMpa().getId(), film2.getMpa().getId());
        assertEquals(filmFromController1.getGenres().size(), film2.getGenres().size());
        assertEquals(new ArrayList<>(filmFromController1.getGenres()).get(0).getId(),
                new ArrayList<>(film2.getGenres()).get(0).getId());
    }

    @Test
    public void shouldReturnNullIfNoTopRatedFilms() {
        assertEquals(filmController.getTopRatedFilms(10).size(), 0);
    }
}
