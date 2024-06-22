package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.model.user.UserEvent;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EventRowMapper implements RowMapper<UserEvent> {
    @Override
    public UserEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
        return UserEvent.builder()
                .timestamp(rs.getTimestamp("timestamp").toLocalDateTime())
                .userId(rs.getLong("user_id"))
                .eventType(rs.getString("event_type"))
                .operation(rs.getString("operation"))
                .eventId(rs.getLong("event_id"))
                .entityId(rs.getLong("entity_id"))
                .build();
    }
}
