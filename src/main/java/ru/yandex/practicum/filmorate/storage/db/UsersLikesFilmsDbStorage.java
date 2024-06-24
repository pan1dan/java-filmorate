package ru.yandex.practicum.filmorate.storage.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.model.UsersLikesFilmsStorage;

@Repository
@Qualifier("usersLikesFilmsDbStorage")
@Primary
public class UsersLikesFilmsDbStorage implements UsersLikesFilmsStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final Logger log = LoggerFactory.getLogger(UsersLikesFilmsDbStorage.class);

    public UsersLikesFilmsDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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
}
