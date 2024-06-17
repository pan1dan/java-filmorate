package ru.yandex.practicum.filmorate.storage.model;

import ru.yandex.practicum.filmorate.model.film.Mpa;

import java.util.List;

public interface FilmRatingMpaStorage {
    List<Mpa> getAllMpa();

    Mpa getMpaById(int id);
}
