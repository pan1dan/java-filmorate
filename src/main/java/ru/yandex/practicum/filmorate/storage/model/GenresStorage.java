package ru.yandex.practicum.filmorate.storage.model;

import ru.yandex.practicum.filmorate.model.film.Genre;

import java.util.List;

public interface GenresStorage {
    List<Genre> getAllGenres();

    Genre getGenreNameById(int id);
}
