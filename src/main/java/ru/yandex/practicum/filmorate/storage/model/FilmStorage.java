package ru.yandex.practicum.filmorate.storage.model;

import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.List;

public interface FilmStorage {
    void deleteFilmByIdFromStorage(Long filmId);

    public List<Film> getAllFilmsFromStorage();

    public Film addNewFilmToStorage(Film film);

    public Film updateFilmInStorage(Film newFilm);

    public Film getFilmByIdFromStorage(Long filmId);
}
