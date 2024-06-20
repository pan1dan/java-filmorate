package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.storage.model.RecommendationStorage;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RecommendationDbStorage implements RecommendationStorage {
    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public List<Film> getRecommendations(final long userId) {
        log.info("Вызов метода recommendationDbStorage.getRecommendations() c userId = {}", userId);
        Optional<Long> similarUserId = getRecommenderUser(userId);
        if (similarUserId.isEmpty()) {
            return Collections.emptyList();
        }
        final String sqlQuery = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration " +
                "FROM films f " +
                "JOIN users_likes_films ul ON f.film_id = ul.film_id " +
                "WHERE ul.user_id = :similarUserId " +
                "AND f.film_id NOT IN (" +
                "SELECT film_id " +
                "FROM users_likes_films " +
                "WHERE user_id = :userId" +
                ") ";

        return jdbc.query(sqlQuery, Map.of("userId", userId, "similarUserId", similarUserId.get()),
                (rs, rowNum) -> Film.builder()
                        .id(rs.getLong("film_id"))
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .releaseDate(rs.getDate("release_date").toLocalDate())
                        .duration(rs.getInt("duration"))
                        .build());
    }

    private Optional<Long> getRecommenderUser(final long userId) {
        log.info("Вызов метода recommendationDbStorage.getRecommenderUser() c userId = {}", userId);
        final String sqlQuery = "SELECT u2.user_id " +
                "FROM users_likes_films u1 " +
                "JOIN users_likes_films u2 ON u1.film_id = u2.film_id " +
                "AND u1.user_id != u2.user_id " +
                "WHERE u1.user_id = :userId " +
                "GROUP BY u2.user_id " +
                "ORDER BY COUNT(*) DESC " +
                "LIMIT 1";
        try {
            return Optional.ofNullable(jdbc.queryForObject(sqlQuery,
                    Map.of("userId", userId), Long.class));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
