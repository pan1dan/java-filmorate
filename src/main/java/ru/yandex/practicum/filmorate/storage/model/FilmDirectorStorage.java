package ru.yandex.practicum.filmorate.storage.model;

import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.List;

public interface FilmDirectorStorage {
    List<Film> getDirectorFilms(Long directorId);
}
