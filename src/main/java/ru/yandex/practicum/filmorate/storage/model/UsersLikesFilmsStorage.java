package ru.yandex.practicum.filmorate.storage.model;

public interface UsersLikesFilmsStorage {
    int getLikesCount(long filmId);

    void addLikeFilm(long filmId, long userId);

    void deleteLikeFilm(long filmId, long userId);
}
