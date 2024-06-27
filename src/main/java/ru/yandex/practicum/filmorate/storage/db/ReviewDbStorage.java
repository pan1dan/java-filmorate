package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.model.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.model.UserEventStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbc;
    private final UserEventStorage userEventDbStorage;

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
        // Выполняем добавление отзыва.
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

        Long reviewId = keyHolder.getKeyAs(Long.class);
        if (reviewId != null) {
            // Записываем событие по добавлению отзыва в БД
            userEventDbStorage.addUserEvent(review.getUserId(),
                    EventType.REVIEW.name(), Operation.ADD.name(), reviewId);
            return reviewId;
        } else {
            throw new ValidationException("Не удалось сохранить данные");
        }
    }

    @Override
    public void updateReview(Review review) {
        // Записываем событие по обновлению отзыва в БД
        userEventDbStorage.addUserEvent(review.getReviewId(),
                EventType.REVIEW.name(), Operation.UPDATE.name(), review.getReviewId());
        // Выполняем обновление отзыва.
        jdbc.update(REQUEST_UPDATE_REVIEW,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());
    }

    @Override
    public boolean deleteReviewById(long id) {
        // проверяем что отзыв есть в БД
        Review review = getReviewById(id);
        // Записываем событие по удалению отзыва в БД
        userEventDbStorage.addUserEvent(review.getUserId(),
                EventType.REVIEW.name(), Operation.REMOVE.name(), review.getReviewId());
        // Выполняем удаление отзыва.
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
        Integer countReviews = jdbc.queryForObject("SELECT COUNT(*) FROM reviews", Integer.class);
        if (countReviews == null || countReviews == 0) {
            return new ArrayList<>();
        }
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