package ru.yandex.practicum.filmorate.service.serviceModel;

import ru.yandex.practicum.filmorate.model.film.Director;

import java.util.List;

public interface DirectorService {
    List<Director> getAllDirectors();

    Director getDirectorById(Long directorId);

    Director addNewDirector(Director director);

    Director updateDirector(Director newDirector);

    void deleteDirector(Long directorId);
}
