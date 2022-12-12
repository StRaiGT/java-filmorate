package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MpaControllerTest {
    private final MpaController mpaController;

    @Test
    public void shouldFindAll() {
        List<Mpa> allMpa = mpaController.getAllMpa();

        assertEquals(allMpa.size(), 5);

        Mpa mpa1 = allMpa.get(0);
        Mpa mpa2 = allMpa.get(1);
        Mpa mpa3 = allMpa.get(2);
        Mpa mpa4 = allMpa.get(3);
        Mpa mpa5 = allMpa.get(4);

        assertEquals(mpa1.getId(), 1);
        assertEquals(mpa1.getName(), "G");
        assertEquals(mpa2.getId(), 2);
        assertEquals(mpa2.getName(), "PG");
        assertEquals(mpa3.getId(), 3);
        assertEquals(mpa3.getName(), "PG-13");
        assertEquals(mpa4.getId(), 4);
        assertEquals(mpa4.getName(), "R");
        assertEquals(mpa5.getId(), 5);
        assertEquals(mpa5.getName(), "NC-17");
    }

    @Test
    public void shouldFindById() {
        Mpa mpa = mpaController.getMpaById(1);

        assertEquals(mpa.getId(), 1);
        assertEquals(mpa.getName(), "G");
    }

    @Test
    public void shouldThrowExceptionIfMpaIdNoFound() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> mpaController.getMpaById(999));
        assertEquals("Рейтинга MPA с таким id не существует.", exception.getMessage());
    }
}
