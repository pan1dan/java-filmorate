package ru.yandex.practicum.filmorate.storage.model;

import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.List;

public interface UsersLikesFilmsStorage {
    int getLikesCount(long filmId);

    void addLikeFilm(long filmId, long userId);

    void deleteLikeFilm(long filmId, long userId);

    public List<Film> getCommonFilms(long userId, long friendId);
}
