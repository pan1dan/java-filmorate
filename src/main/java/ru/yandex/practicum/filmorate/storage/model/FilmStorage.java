package ru.yandex.practicum.filmorate.storage.model;

import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getAllFilm();

    void deleteFilmByIdFromStorage(Long filmId);

    Film create(Film film);

    Film update(Film newFilm);

    Film getFilmById(Long filmId);
}
