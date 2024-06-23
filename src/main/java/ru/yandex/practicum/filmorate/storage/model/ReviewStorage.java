package ru.yandex.practicum.filmorate.storage.model;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {

    long createReview(Review review);

    void updateReview(Review review);

    boolean deleteReviewById(long id);

    Review getReviewById(long id);

    List<Review> getReviewsFilm(long filmId, int count);

    List<Review> getPoolReviews(int count);

    void setLike(long reviewId, long userId);

    void updateLike(long reviewId, long userId);

    void removeLike(long reviewId, long userId);

    void setDislike(long reviewId, long userId);

    void updateDislike(long reviewId, long userId);

    void removeDislike(long reviewId, long userId);
}