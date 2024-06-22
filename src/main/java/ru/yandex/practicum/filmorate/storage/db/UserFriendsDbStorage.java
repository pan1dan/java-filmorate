package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InsertDbException;
import ru.yandex.practicum.filmorate.model.user.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.model.UserFriendsStorage;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Qualifier("userFriendsDbStorage")
@Primary
@Slf4j
@RequiredArgsConstructor
public class UserFriendsDbStorage implements UserFriendsStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(long user1Id, long user2Id) {
        try {
            // Записываем событие по добавлению друга в БД
            LocalDateTime timeOperation = LocalDateTime.now();
            String sqlAddEvent = "INSERT INTO user_events(timestamp, user_id, event_type, operation, entity_id) " +
                    "VALUES (?, ?, ?, ?, ?)";
            int countUpd = jdbcTemplate.update(sqlAddEvent, timeOperation, user1Id, "FRIEND", "ADD", user2Id);
            if(countUpd != 1){
                throw new InsertDbException("Ошибка при записи в БД события по добавлению друга");
            }
            // Выполняем добавление друга.
            String sql = "INSERT INTO user_friends(user_id, status, friend_id) " +
                    "VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, user1Id, FriendshipStatus.UNCONFIRMED.name(), user2Id);
        } catch (Exception e) {
            log.warn("Ошибка при добавлении друга в БД", e);
            throw new RuntimeException("Ошибка при добавлении друга в БД", e);
        }
    }

    @Override
    public void deleteFriend(long user1Id, long user2Id) {
        try {
            // Записываем событие по удалению друга в БД
            LocalDateTime timeOperation = LocalDateTime.now();
            String sqlAddEvent = "INSERT INTO user_events(timestamp, user_id, event_type, operation, entity_id) " +
                    "VALUES (?, ?, ?, ?, ?)";
            int countUpd = jdbcTemplate.update(sqlAddEvent, timeOperation, user1Id, "FRIEND", "REMOVE", user2Id);
            if(countUpd != 1){
                throw new InsertDbException("Ошибка при записи в БД события по удалению друга");
            }
            // Выполняем удаление друга.
            String sql =  "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";
            jdbcTemplate.update(sql, user1Id, user2Id);
        } catch (Exception e) {
            log.warn("Ошибка при удалении друга в БД", e);
            throw new RuntimeException("Ошибка при удалении друга в БД", e);
        }
    }

    @Override
    public List<User> getFriends(long userId) {
        try {
            String sql = "SELECT * " +
                    "FROM users " +
                    "WHERE user_id IN (" +
                        "SELECT friend_id " +
                        "FROM user_friends " +
                        "WHERE user_id = ?)";
            return jdbcTemplate.query(sql, new UserRowMapper(), userId);
        } catch (Exception e) {
            log.warn("Ошибка при получении списка всех друзей пользователя из БД", e);
            throw new RuntimeException("Ошибка при получении списка всех друзей пользователя из БД", e);
        }
    }

}
