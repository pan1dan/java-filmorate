package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review createReview(@RequestBody @Valid Review review) {
        log.info("GET /reviews");
        return reviewService.createReview(review);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Review updateReview(@RequestBody @Valid Review review) {
        log.info("PUT /reviews");
        log.info("Received review with userId: " + review.getUserId());
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public boolean deleteReviewById(@PathVariable long id) {
        log.info("DELETE /reviews/{}", id);
        return reviewService.deleteReviewById(id);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Review getReviewById(@PathVariable long id) {
        log.info("GET /reviews/{}", id);
        return reviewService.getReviewById(id);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<Review> getReviews(@RequestParam(required = false) Long filmId,
                                   @RequestParam(defaultValue = "10") int count) {
        log.info("GET /reviews.?{} {}", filmId, count);
        return reviewService.getReviews(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Review likeReview(@PathVariable long id,
                             @PathVariable long userId) {
        log.info("PUT /reviews/{}/like/{}", id, userId);
        return reviewService.likeReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Review dislikeReview(@PathVariable long id,
                                @PathVariable long userId) {
        log.info("PUT /reviews/{}/dislike/{}", id, userId);
        return reviewService.dislikeReview(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Review deleteLike(@PathVariable long id,
                             @PathVariable long userId) {
        log.info("DELETE /reviews/{}/like/{}", id, userId);
        return reviewService.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Review deleteDislike(@PathVariable long id,
                                @PathVariable long userId) {
        log.info("DELETE /reviews/{}/dislike/{}", id, userId);
        return reviewService.deleteDislike(id, userId);
    }
}