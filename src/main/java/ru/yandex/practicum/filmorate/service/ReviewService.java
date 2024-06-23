package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.model.*;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final LikeStateStorage usabilityStateStorage;

    public Review createReview(Review review) {
        log.info("Начало работы метода createReview");
        checkUser(review.getUserId());
        checkFilm(review.getFilmId());
        long id = reviewStorage.createReview(review);
        review.setReviewId(id);
        return review;
    }

    public Review updateReview(Review review) {
        log.info("Начало работы метода updateReview");
        checkReview(review.getReviewId());
        checkUser(review.getUserId());
        checkFilm(review.getFilmId());
        reviewStorage.updateReview(review);
        review = getReviewById(review.getReviewId());
        return review;
    }

    public boolean deleteReviewById(long reviewId) {
        log.info("Начало работы метода deleteReview");
        checkReview(reviewId);
        return reviewStorage.deleteReviewById(reviewId);
    }

    public Review getReviewById(long reviewId) {
        log.info("Начало работы метода getReview {}", reviewId);
        checkReview(reviewId);
        return reviewStorage.getReviewById(reviewId);
    }

    public List<Review> getReviews(long filmId, int count) {
        log.info("Начало работы метода getReviews filmId {} count {}", filmId, count);
        if (filmId >= 0) {
            checkFilm(filmId);
            return reviewStorage.getReviewsFilm(filmId, count);
        }
        return reviewStorage.getPoolReviews(count);
    }

    public Review likeReview(long reviewId, long userId) {
        log.info("Начало работы метода likeReview reviewId {} userId {}", reviewId, userId);
        checkReview(reviewId);
        checkUser(userId);
        int state = usabilityStateStorage.getCurrentState(reviewId, userId).orElse(0);
        if (state == 0) {
            reviewStorage.setLike(reviewId, userId);
        } else if (state == 2) {
            reviewStorage.updateLike(reviewId, userId);
        }
        return getReviewById(reviewId);
    }

    public Review dislikeReview(long reviewId, long userId) {
        log.info("Начало работы метода dislikeReview reviewId {} userId {}", reviewId, userId);
        checkReview(reviewId);
        checkUser(userId);
        int state = usabilityStateStorage.getCurrentState(reviewId, userId).orElse(0);
        if (state == 0) {
            reviewStorage.setDislike(reviewId, userId);
        } else if (state == 1) {
            reviewStorage.updateDislike(reviewId, userId);
        }
        return getReviewById(reviewId);
    }

    public Review deleteLike(long reviewId, long userId) {
        log.info("Начало работы метода deleteLike reviewId {} userId {}", reviewId, userId);
        checkReview(reviewId);
        checkUser(userId);
        reviewStorage.removeLike(reviewId, userId);
        return getReviewById(reviewId);
    }

    public Review deleteDislike(long reviewId, long userId) {
        log.info("Начало работы метода deleteDislike reviewId {} userId {}", reviewId, userId);
        checkReview(reviewId);
        checkUser(userId);
        reviewStorage.removeDislike(reviewId, userId);
        return getReviewById(reviewId);
    }

    private void checkUser(long userId) {
        log.info("Начало работы метода checkUser userId {}", userId);
        userStorage.getUserByIdFromStorage(userId);
    }

    private void checkFilm(long filmId) {
        log.info("Начало работы метода checkFilm filmId {}", filmId);
        filmStorage.getFilmByIdFromStorage(filmId);
    }

    private void checkReview(long reviewId) {
        log.info("Начало работы метода checkReview reviewId {}", reviewId);
        reviewStorage.getReviewById(reviewId);
    }
}