package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class GenreControllerTest {
    private final GenreController genreController;

    @Test
    public void shouldFindAll() {
        List<Genre> allGenres = genreController.getAllGenres();

        assertEquals(allGenres.size(), 6);

        Genre genre1 = allGenres.get(0);
        Genre genre2 = allGenres.get(1);
        Genre genre3 = allGenres.get(2);
        Genre genre4 = allGenres.get(3);
        Genre genre5 = allGenres.get(4);
        Genre genre6 = allGenres.get(5);

        assertEquals(genre1.getId(), 1);
        assertEquals(genre1.getName(), "Комедия");
        assertEquals(genre2.getId(), 2);
        assertEquals(genre2.getName(), "Драма");
        assertEquals(genre3.getId(), 3);
        assertEquals(genre3.getName(), "Мультфильм");
        assertEquals(genre4.getId(), 4);
        assertEquals(genre4.getName(), "Триллер");
        assertEquals(genre5.getId(), 5);
        assertEquals(genre5.getName(), "Документальный");
        assertEquals(genre6.getId(), 6);
        assertEquals(genre6.getName(), "Боевик");
    }

    @Test
    public void shouldFindById() {
        Genre genre = genreController.getGenreById(1);

        assertEquals(genre.getId(), 1);
        assertEquals(genre.getName(), "Комедия");
    }

    @Test
    public void shouldThrowExceptionIfMpaIdNoFound() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> genreController.getGenreById(999));
        assertEquals("Жанра с таким id не существует.", exception.getMessage());
    }
}
