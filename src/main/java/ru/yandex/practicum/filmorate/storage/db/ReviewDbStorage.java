package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.model.ReviewStorage;

import java.sql.*;
import java.util.*;

@Slf4j
@Component
@Primary
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbc;

    public ReviewDbStorage(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final String REQUEST_ADD_REVIEW = """
            INSERT INTO reviews (film_id, user_id, content, is_positive)
            VALUES (?, ?, ?, ?);
            """;

    private static final String REQUEST_UPDATE_REVIEW = """
            UPDATE reviews
            SET content = ?, is_positive = ?
            WHERE review_id = ?;
            """;

    private static final String REQUEST_DELETE_REVIEW = """
            DELETE FROM reviews
            WHERE review_id = ?;
            """;

    private static final String REQUEST_GET_REVIEW = """
            SELECT
                r.review_id AS review_id,
                r.film_id AS film_id,
                r.user_id AS user_id,
                r.content AS content,
                r.is_positive AS is_positive,
                SUM(u.weigh) AS useful
            FROM reviews AS r
            LEFT JOIN usability_reviews AS ur ON r.review_id = ur.review_id
            LEFT JOIN usabilitys AS u ON ur.usability_id = u.usability_id
            WHERE r.review_id = ?
            GROUP BY r.review_id;
            """;

    private static final String REQUEST_GET_ALL_REVIEWS_FOR_FILM = """
            SELECT
                r.review_id AS review_id,
                r.film_id AS film_id,
                r.user_id AS user_id,
                r.content AS content,
                r.is_positive AS is_positive,
                COALESCE(SUM(u.weigh), 0) AS useful
            FROM reviews AS r
            LEFT JOIN usability_reviews AS ur ON r.review_id = ur.review_id
            LEFT JOIN usabilitys AS u ON ur.usability_id = u.usability_id
            WHERE r.film_id = ?
            GROUP BY r.review_id
            ORDER BY useful DESC, r.review_id
            LIMIT ?;
            """;

    private static final String REQUEST_GET_ALL_REVIEWS_FOR_ALL_FILMS = """
            WITH RankedReviews AS (
                SELECT
                    review_id,
                    film_id,
                    user_id,
                    content,
                    is_positive,
                    ROW_NUMBER() OVER (PARTITION BY film_id ORDER BY review_id) AS rn
                FROM reviews
            )
            SELECT rr.*, COALESCE(SUM(u.weigh), 0) AS useful
            FROM RankedReviews AS rr
            LEFT JOIN usability_reviews AS ur ON rr.review_id = ur.review_id
            LEFT JOIN usabilitys AS u ON ur.usability_id = u.usability_id
            WHERE rn <= ?
            GROUP BY rr.review_id
            ORDER BY useful DESC, rr.film_id, rn;
            """;

    private static final String REQUEST_SET_LIKE = """
            INSERT INTO usability_reviews (user_id, review_id, usability_id)
            VALUES (?, ?, 1);
            """;

    private static final String REQUEST_SET_DISLIKE = """
            INSERT INTO usability_reviews (user_id, review_id, usability_id)
            VALUES (?, ?, 2);
            """;

    private static final String REQUEST_UPDATE_TO_LIKE = """
            UPDATE usability_reviews
            SET usability_id = 1
            WHERE user_id = ? AND review_id = ?;
            """;

    private static final String REQUEST_UPDATE_TO_DISLIKE = """
            UPDATE usability_reviews
            SET usability_id = 2
            WHERE user_id = ? AND review_id = ?;
            """;

    private static final String REQUEST_REMOVE_LIKE = """
            DELETE FROM usability_reviews
            WHERE user_id = ? AND review_id = ? AND usability_id = 1;
            """;

    private static final String REQUEST_REMOVE_DISLIKE = """
            DELETE FROM usability_reviews
            WHERE user_id = ? AND review_id = ? AND usability_id = 2;
            """;

    @Override
    public long createReview(Review review) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(REQUEST_ADD_REVIEW, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, review.getFilmId());
            ps.setObject(2, review.getUserId());
            ps.setObject(3, review.getContent());
            ps.setObject(4, review.getIsPositive());
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);
        if (id != null) {
            return id;
        } else {
            throw new ValidationException("Не удалось сохранить данные");
        }
    }

    @Override
    public void updateReview(Review review) {
        jdbc.update(REQUEST_UPDATE_REVIEW,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());
    }

    @Override
    public boolean deleteReviewById(long id) {
        int rowsDeleted = jdbc.update(REQUEST_DELETE_REVIEW, id);
        return rowsDeleted > 0;
    }

    @Override
    public Review getReviewById(long id) {
        try {
            return jdbc.queryForObject(REQUEST_GET_REVIEW, this::mapRow, id);
        } catch (Exception e) {
            log.info("Отзыва с id-->{} нет в БД", id);
            throw new NotFoundException(String.format("Отзыв с id = %s не найден", id));
        }
    }

    @Override
    public List<Review> getReviewsFilm(long filmId, int count) {
        return jdbc.query(REQUEST_GET_ALL_REVIEWS_FOR_FILM, this::mapRow, filmId, count);
    }

    @Override
    public List<Review> getPoolReviews(int count) {
        return jdbc.query(REQUEST_GET_ALL_REVIEWS_FOR_ALL_FILMS, this::mapRow, count);
    }

    @Override
    public void setLike(long reviewId, long userId) {
        jdbc.update(REQUEST_SET_LIKE, userId, reviewId);
    }

    @Override
    public void updateLike(long reviewId, long userId) {
        jdbc.update(REQUEST_UPDATE_TO_LIKE, userId, reviewId);
    }

    @Override
    public void setDislike(long reviewId, long userId) {
        jdbc.update(REQUEST_SET_DISLIKE, userId, reviewId);
    }

    @Override
    public void updateDislike(long reviewId, long userId) {
        jdbc.update(REQUEST_UPDATE_TO_DISLIKE, userId, reviewId);
    }

    @Override
    public void removeLike(long reviewId, long userId) {
        jdbc.update(REQUEST_REMOVE_LIKE, userId, reviewId);
    }

    @Override
    public void removeDislike(long reviewId, long userId) {
        jdbc.update(REQUEST_REMOVE_DISLIKE, userId, reviewId);
    }

    private Review mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(rs.getLong("review_id"))
                .filmId(rs.getLong("film_id"))
                .userId(rs.getLong("user_id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .useful(rs.getInt("useful"))
                .build();
    }
}