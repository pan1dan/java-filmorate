package ru.yandex.practicum.filmorate.storage.model;

import ru.yandex.practicum.filmorate.model.film.Director;

import java.util.List;

public interface DirectorStorage {

    List<Director> getAllDirectors();

    Director getDirectorById(Long id);

    Director addNewDirector(Director director);

    Director updateDirector(Director newDirector);

    void deleteDirector(Long directorId);
}
