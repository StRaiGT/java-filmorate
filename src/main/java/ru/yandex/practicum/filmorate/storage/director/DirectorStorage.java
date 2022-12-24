package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {
    Director createDirector(Director director);

    Director updateDirector(Director director);

    Boolean deleteDirector(int directorId);

    Director getDirector(int directorId);

    List<Director> getAllDirectors();
}
