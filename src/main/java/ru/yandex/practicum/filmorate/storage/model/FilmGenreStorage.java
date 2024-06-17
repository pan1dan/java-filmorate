package ru.yandex.practicum.filmorate.storage.model;

import ru.yandex.practicum.filmorate.model.film.Genre;

import java.util.List;

public interface FilmGenreStorage {
    List<Genre> getAllFilmsGenres(Long id);
}
