package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DirectorControllerTest {
    private final DirectorController directorController;

    @Test
    public void shouldGetAllDirectors() {
        Director director1 = Director.builder()
                .id(1)
                .name("director 1")
                .build();
        directorController.createDirector(director1);

        Director director2 = Director.builder()
                .id(2)
                .name("director 2")
                .build();
        directorController.createDirector(director2);

        List<Director> directorsFromController = directorController.getAllDirectors();

        assertEquals(directorsFromController.size(), 2);
        assertEquals(directorsFromController.get(0).getId(), director1.getId());
        assertEquals(directorsFromController.get(0).getName(), director1.getName());
        assertEquals(directorsFromController.get(1).getId(), director2.getId());
        assertEquals(directorsFromController.get(1).getName(), director2.getName());
    }

    @Test
    public void shouldGetNullDirectors() {
        List<Director> directorsFromController = directorController.getAllDirectors();

        assertEquals(directorsFromController.size(), 0);
    }

    @Test
    public void shouldCreateAndGetDirectorById() {
        Director director1 = Director.builder()
                .id(1)
                .name("director 1")
                .build();
        directorController.createDirector(director1);

        Director directorFromController = directorController.getDirectorById(1);

        assertEquals(directorFromController.getId(), director1.getId());
        assertEquals(directorFromController.getName(), director1.getName());
    }

    @Test
    public void shouldThrowExceptionIfDirectorNotFound() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> directorController.getDirectorById(999));
        assertEquals("Режиссера с таким id не существует.", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionIfAddDirectorNameFound() {
        Director director1 = Director.builder()
                .id(1)
                .name("director 1")
                .build();
        directorController.createDirector(director1);

        Director director2 = Director.builder()
                .id(2)
                .name("director 1")
                .build();

        AlreadyExistException exception = assertThrows(AlreadyExistException.class, () -> directorController.createDirector(director2));
        assertEquals("Режиссер с таким именем уже существует.", exception.getMessage());

        List<Director> directorsFromController = directorController.getAllDirectors();

        assertEquals(directorsFromController.size(), 1);
        assertEquals(directorsFromController.get(0).getId(), director1.getId());
        assertEquals(directorsFromController.get(0).getName(), director1.getName());
    }

    @Test
    public void shouldUpdateDirector() {
        Director director1 = Director.builder()
                .id(1)
                .name("director 1")
                .build();
        directorController.createDirector(director1);

        Director director2 = Director.builder()
                .id(1)
                .name("director update")
                .build();
        directorController.updateDirector(director2);

        List<Director> directorsFromController = directorController.getAllDirectors();

        assertEquals(directorsFromController.size(), 1);
        assertEquals(directorsFromController.get(0).getId(), director2.getId());
        assertEquals(directorsFromController.get(0).getName(), director2.getName());
    }

    @Test
    public void shouldThrowExceptionIfUpdateDirectorNameFound() {
        Director director1 = Director.builder()
                .id(1)
                .name("director 1")
                .build();
        directorController.createDirector(director1);

        Director director2 = Director.builder()
                .id(2)
                .name("director 2")
                .build();
        directorController.createDirector(director2);

        Director director3 = Director.builder()
                .id(1)
                .name("director 2")
                .build();

        AlreadyExistException exception = assertThrows(AlreadyExistException.class, () -> directorController.updateDirector(director3));
        assertEquals("Режиссер с таким именем уже существует.", exception.getMessage());

        List<Director> directorsFromController = directorController.getAllDirectors();

        assertEquals(directorsFromController.size(), 2);
        assertEquals(directorsFromController.get(0).getId(), director1.getId());
        assertEquals(directorsFromController.get(0).getName(), director1.getName());
        assertEquals(directorsFromController.get(1).getId(), director2.getId());
        assertEquals(directorsFromController.get(1).getName(), director2.getName());
    }

    @Test
    public void shouldThrowExceptionIfUpdateDirectorIdNotFound() {
        Director director1 = Director.builder()
                .id(1)
                .name("director 1")
                .build();
        directorController.createDirector(director1);

        Director director2 = Director.builder()
                .id(2)
                .name("director 2")
                .build();

        NotFoundException exception = assertThrows(NotFoundException.class, () -> directorController.updateDirector(director2));
        assertEquals("Режиссера с таким id не существует.", exception.getMessage());

        List<Director> directorsFromController = directorController.getAllDirectors();

        assertEquals(directorsFromController.size(), 1);
        assertEquals(directorsFromController.get(0).getId(), director1.getId());
        assertEquals(directorsFromController.get(0).getName(), director1.getName());
    }

    @Test
    public void shouldDeleteDirector() {
        Director director1 = Director.builder()
                .id(1)
                .name("director 1")
                .build();
        directorController.createDirector(director1);

        Director director2 = Director.builder()
                .id(2)
                .name("director 2")
                .build();
        directorController.createDirector(director2);

        List<Director> directorsFromController = directorController.getAllDirectors();

        assertEquals(directorsFromController.size(), 2);
        assertEquals(directorsFromController.get(0).getId(), director1.getId());
        assertEquals(directorsFromController.get(0).getName(), director1.getName());
        assertEquals(directorsFromController.get(1).getId(), director2.getId());
        assertEquals(directorsFromController.get(1).getName(), director2.getName());

        directorController.deleteDirector(1);

        directorsFromController = directorController.getAllDirectors();

        assertEquals(directorsFromController.size(), 1);
        assertEquals(directorsFromController.get(0).getId(), director2.getId());
        assertEquals(directorsFromController.get(0).getName(), director2.getName());
    }

    @Test
    public void shouldNoThrowExceptionIfDeleteDirectorNotFound() {
        directorController.deleteDirector(1);
        List<Director> directorsFromController = directorController.getAllDirectors();

        assertEquals(directorsFromController.size(), 0);
    }
}
