package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InsertDbException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.user.UserEvent;
import ru.yandex.practicum.filmorate.storage.mapper.EventRowMapper;
import ru.yandex.practicum.filmorate.storage.model.UserEventStorage;

import java.time.Instant;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class UserEventDbStorage implements UserEventStorage {

    private final JdbcTemplate jdbc;

    @Override
    public List<UserEvent> getUserEvents(long userId) {
        try {
            String queryFeed = "SELECT * " +
                    "FROM user_events " +
                    "WHERE user_id = ? " +
                    "ORDER BY timestamp";
            return jdbc.query(queryFeed, new EventRowMapper(), userId);
        } catch (Exception e) {
            log.warn("Ошибка при получении из БД ленты событий пользователя с id = {}", userId, e);
            throw new NotFoundException("Ошибка при получении из БД ленты событий пользователя id = " + userId);
        }
    }

    @Override
    public void addUserEvent(long userId, String eventType, String operation, long entityId) {
        long timeOperation = Instant.now().toEpochMilli();
        String sqlAddEvent = "INSERT INTO user_events(timestamp, user_id, event_type, operation, entity_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        int countUpd = jdbc.update(sqlAddEvent, timeOperation, userId, eventType, operation, entityId);
        if (countUpd != 1) {
            throw new InsertDbException("Ошибка при записи операции: " + operation + " с сущностью: " + eventType +
                    " и entityId=" + entityId + " в БД. Событие для пользователя с userId=" + userId);
        }

    }
}
