package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.UserLikesFilms;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.mapper.UserLikesFilmsMapper;
import ru.yandex.practicum.filmorate.storage.model.UserEventStorage;
import ru.yandex.practicum.filmorate.storage.model.UsersLikesFilmsStorage;

import java.util.List;

@Repository
@Qualifier("usersLikesFilmsDbStorage")
@Primary
@Slf4j
@RequiredArgsConstructor
public class UsersLikesFilmsDbStorage implements UsersLikesFilmsStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserEventStorage userEventDbStorage;

    @Override
    public int getLikesCount(long filmId) {
        try {
            String sql = "SELECT COUNT(user_id) FROM users_likes_films WHERE film_id = ?";
            return jdbcTemplate.queryForObject(sql, Integer.class, filmId);
        } catch (Exception e) {
            log.warn("Ошибка при получении количество лайков фильма из БД", e);
            throw new RuntimeException("Ошибка при получении количество лайков фильма из БД", e);
        }
    }

    @Override
    public void addLikeFilm(long filmId, long userId) {
        try {
            // Записываем событие по добавлению лайка в БД
            userEventDbStorage.addUserEvent(userId, EventType.LIKE.name(), Operation.ADD.name(), filmId);
            // Выполняем добавление лайка.
            String sql = "INSERT INTO users_likes_films(film_id, user_id) VALUES (?, ?)";
            jdbcTemplate.update(sql, filmId, userId);
        } catch (Exception e) {
            log.warn("Ошибка при получении количество лайков фильма из БД", e);
            throw new RuntimeException("Ошибка при получении количество лайков фильма из БД", e);
        }
    }

    @Override
    public void deleteLikeFilm(long filmId, long userId) {
        try {
            // Записываем событие по удалению лайка в БД
            userEventDbStorage.addUserEvent(userId, EventType.LIKE.name(), Operation.REMOVE.name(), filmId);
            // Выполняем удаление лайка.
            String sql = "DELETE FROM users_likes_films WHERE film_id = ? AND user_id = ?";
            int rowsUpdated = jdbcTemplate.update(sql, filmId, userId);
            if (rowsUpdated == 0) {
                log.warn("Указанная пара фильм(id = {}) - пользователь(id = {}) не найдена в БД", filmId, userId);
                throw new NotFoundException("Указанная пара фильм - пользователь не найдена в БД");
            }
        } catch (NotFoundException e) {
            log.warn("Указанная пара фильм(id = {}) - пользователь(id = {}) не найдена в БД", filmId, userId);
            throw new NotFoundException("Указанная пара фильм - пользователь не найдена в БД");
        } catch (Exception e) {
            log.warn("Ошибка при получении количество лайков фильма из БД", e);
            throw new RuntimeException("Ошибка при получении количество лайков фильма из БД", e);
        }
    }

    @Override
    public List<UserLikesFilms> getUserFilms(long userId) {
        try {
            String getUserLikesFilmsSql = "SELECT user_id, film_id FROM users_likes_films WHERE user_id = ?";
            return jdbcTemplate.query(getUserLikesFilmsSql, new UserLikesFilmsMapper(), userId);
        } catch (Exception e) {
            log.warn("Ошибка при получении списка лайкнутых фильмов из БД", e);
            throw new RuntimeException("Ошибка при получении списка лайкнутых фильмов из БД", e);
        }

    }
}
