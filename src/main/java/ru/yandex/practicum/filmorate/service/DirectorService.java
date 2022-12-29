package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectorService {
    private final DirectorStorage directorStorage;

    public Director createDirector(Director director) {
        log.info("Добавление режиссера {}", director);
        return directorStorage.createDirector(director);
    }

    public Director updateDirector(Director director) {
        log.info("Обновление режиссера {}", director);
        return directorStorage.updateDirector(director);
    }

    public Boolean deleteDirector(int directorId) {
        log.info("Удаление режиссера {}", directorId);
        return directorStorage.deleteDirector(directorId);
    }

    public Director getDirectorById(int directorId) {
        log.info("Вывод режиссера с id {}", directorId);
        return directorStorage.getDirector(directorId);
    }

    public List<Director> getAllDirectors() {
        log.info("Вывод всех режиссеров.");
        return directorStorage.getAllDirectors();
    }
}
