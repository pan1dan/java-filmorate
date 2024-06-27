package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.model.LikeStateStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Slf4j
@Component
@Primary
public class LikeStateDbStorage implements LikeStateStorage {

    private final JdbcTemplate jdbc;

    private static final String REQUEST_GET_USABILITY_STATE = """
            SELECT usability_id
            FROM usability_reviews
            WHERE review_id = ? AND user_id = ?
            """;

    public LikeStateDbStorage(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<Integer> getCurrentState(long reviewId, long userId) {
        try {
            Integer result = jdbc.queryForObject(REQUEST_GET_USABILITY_STATE, this::mapRow, reviewId, userId);
            System.out.println(result);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    private Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rs.getInt("usability_id");
    }
}