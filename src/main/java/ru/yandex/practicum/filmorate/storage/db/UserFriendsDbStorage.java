package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.user.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.model.UserFriendsStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Qualifier("userFriendsDbStorage")
@Primary
@Slf4j
public class UserFriendsDbStorage implements UserFriendsStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserFriendsDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addUserFriend(long user1Id, long user2Id) {
        try {
            String sql = "INSERT INTO user_friends(user_id, status, friend_id) " +
                    "VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, user1Id, FriendshipStatus.UNCONFIRMED.name(), user2Id);
        } catch (Exception e) {
            log.warn("Ошибка при добавлении друга в БД", e);
            throw new RuntimeException("Ошибка при добавлении друга в БД", e);
        }
    }

    @Override
    public void deleteUserFriends(long user1Id, long user2Id) {
        try {
            String sql =  "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";
            jdbcTemplate.update(sql, user1Id, user2Id);
        } catch (Exception e) {
            log.warn("Ошибка при удалении друга в БД", e);
            throw new RuntimeException("Ошибка при удалении друга в БД", e);
        }
    }

    @Override
    public List<User> getUserFriends(long userId) {
        try {
            String sql = "SELECT * " +
                    "FROM users " +
                    "WHERE user_id IN (" +
                        "SELECT friend_id " +
                        "FROM user_friends " +
                        "WHERE user_id = ?)";
            return jdbcTemplate.query(sql, this::mapRow, userId);
        } catch (Exception e) {
            log.warn("Ошибка при получении списка всех друзей пользователя из БД", e);
            throw new RuntimeException("Ошибка при получении списка всех друзей пользователя из БД", e);
        }
    }

    private User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getLong("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }


}
