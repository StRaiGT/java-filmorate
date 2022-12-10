package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilmControllerTest {
    private FilmController controller;
    private UserStorage userStorage;

    /*@BeforeEach
    public void createController() {
        userStorage = new InMemoryUserStorage();
        FilmStorage filmStorage = new InMemoryFilmStorage();
        FilmService filmService = new FilmService(filmStorage);
        controller = new FilmController(filmService);
    }

    @Test
    public void shouldAddFilmWithValidFields() throws ValidationException{
        Film film = Film.builder()
                .name("test film name")
                .description("description")
                .duration(100)
                .releaseDate(LocalDate.of(1967, 3, 25))
                .build();
        controller.create(film);
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
        controller.create(film);

        Film newFilm = Film.builder()
                .id(1)
                .name("test film name")
                .description("description")
                .duration(100)
                .releaseDate(LocalDate.of(1967, 3, 25))
                .build();

        FilmAlreadyExistException exception = assertThrows(FilmAlreadyExistException.class, () -> {controller.create(newFilm);});
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
        ValidationException exception = assertThrows(ValidationException.class, () -> {controller.create(film);});
        assertEquals("Дата релиза фильма не может быть раньше " + FilmService.FIRST_FILM_RELEASE_DATE,
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
        controller.create(film);

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
        controller.create(film);

        Film updatedFilm = Film.builder()
                .id(999)
                .name("update film name")
                .description("update description")
                .duration(200)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .build();

        FilmNotFoundException exception = assertThrows(FilmNotFoundException.class, () -> {controller.update(updatedFilm);});
        assertEquals("Фильма с таким id не существует.", exception.getMessage());
        assertEquals(controller.getAll().size(), 1);
    }

    @Test
    public void shouldGetAllFilms() {
        Film film1 = Film.builder()
                .id(1)
                .name("test film name 1")
                .description("description 1")
                .duration(100)
                .releaseDate(LocalDate.of(1967, 3, 25))
                .build();
        controller.create(film1);

        Film film2 = Film.builder()
                .id(2)
                .name("test film name 2")
                .description("description 2")
                .duration(100)
                .releaseDate(LocalDate.of(1987, 8, 5))
                .build();
        controller.create(film2);
        List<Film> arr = new ArrayList<>(controller.getAll());

        assertEquals(arr.size(), 2);
        assertEquals(arr.get(0), film1);
        assertEquals(arr.get(1), film2);
    }

    @Test
    public void shouldGetEmptyIfNoFilms() {
        List<Film> arr = new ArrayList<>(controller.getAll());

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
                .build();
        controller.create(film);
        Film filmFromController = controller.getFilmById(1);

        assertEquals(filmFromController, film);
    }

    @Test
    public void shouldThrowExceptionIfFilmIdNotFound() {
        FilmNotFoundException exception = assertThrows(FilmNotFoundException.class, () -> {controller.getFilmById(999);});
        assertEquals("Фильма с таким id не существует.", exception.getMessage());
    }

    @Test
    public void shouldAddLike() {
        Film film = Film.builder()
                .id(1)
                .name("test film name")
                .description("description")
                .duration(100)
                .releaseDate(LocalDate.of(1967, 3, 25))
                .build();
        controller.create(film);

        User user = User.builder()
                .id(1)
                .email("tester@yandex.ru")
                .name("Test name")
                .login("ValidTestLogin")
                .birthday(LocalDate.of(1964, 6, 11))
                .build();
        userStorage.createUser(user);

        controller.addLike(1, 1);
        assertEquals(new ArrayList<>(controller.getAll()).get(0).getLikes().size(), 1);
        assertTrue(new ArrayList<>(controller.getAll()).get(0).getLikes().contains(1));
    }

    @Test
    public void shouldRemoveLike() {
        Film film = Film.builder()
                .id(1)
                .name("test film name")
                .description("description")
                .duration(100)
                .releaseDate(LocalDate.of(1967, 3, 25))
                .build();
        controller.create(film);

        User user = User.builder()
                .id(1)
                .email("tester@yandex.ru")
                .name("Test name")
                .login("ValidTestLogin")
                .birthday(LocalDate.of(1964, 6, 11))
                .build();
        userStorage.createUser(user);

        controller.addLike(1, 1);
        controller.removeLike(1, 1);
        assertEquals(new ArrayList<>(controller.getAll()).get(0).getLikes().size(), 0);
        assertFalse(new ArrayList<>(controller.getAll()).get(0).getLikes().contains(1));
    }

    @Test
    public void shouldThrowExceptionRemoveLikeIfNotLiked() {
        Film film = Film.builder()
                .id(1)
                .name("test film name")
                .description("description")
                .duration(100)
                .releaseDate(LocalDate.of(1967, 3, 25))
                .build();
        controller.create(film);

        User user = User.builder()
                .id(1)
                .email("tester@yandex.ru")
                .name("Test name")
                .login("ValidTestLogin")
                .birthday(LocalDate.of(1964, 6, 11))
                .build();
        userStorage.createUser(user);

        UserNotLikeFilmException exception = assertThrows(UserNotLikeFilmException.class,
                () -> {controller.removeLike(1, 1);});
        assertEquals("Пользователь не поставил лайк фильму.", exception.getMessage());
    }

    @Test
    public void shouldGetTopRatedFilms() {
        Film film1 = Film.builder()
                .id(1)
                .name("test film name")
                .description("description")
                .duration(100)
                .releaseDate(LocalDate.of(1967, 3, 25))
                .build();
        controller.create(film1);

        Film film2 = Film.builder()
                .id(2)
                .name("test film name")
                .description("description")
                .duration(100)
                .releaseDate(LocalDate.of(1967, 3, 25))
                .build();
        controller.create(film2);

        Film film3 = Film.builder()
                .id(3)
                .name("test film name")
                .description("description")
                .duration(100)
                .releaseDate(LocalDate.of(1967, 3, 25))
                .build();
        controller.create(film3);

        User user1 = User.builder()
                .id(1)
                .email("tester@yandex.ru")
                .name("Test name")
                .login("ValidTestLogin")
                .birthday(LocalDate.of(1964, 6, 11))
                .build();
        userStorage.createUser(user1);

        User user2 = User.builder()
                .id(2)
                .email("tester@yandex.ru")
                .name("Test name")
                .login("ValidTestLogin")
                .birthday(LocalDate.of(1964, 6, 11))
                .build();
        userStorage.createUser(user2);

        controller.addLike(2, 1);
        controller.addLike(3, 1);
        controller.addLike(3, 2);
        List<Film> listFilms = controller.getTopRatedFilms(2);

        assertEquals(listFilms.size(), 2);
        assertEquals(listFilms.get(0), film3);
        assertEquals(listFilms.get(1), film2);
    }*/
}
