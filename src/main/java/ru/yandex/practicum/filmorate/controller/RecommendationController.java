package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.service.interfaces.RecommendationService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class RecommendationController {
    private final RecommendationService recommendationService;

    @GetMapping("/{id}/recommendations")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getRecommendations(@PathVariable(name = "id") final long userId) {
        log.info("request GET /users/{}/recommendations", userId);
        final List<Film> result = recommendationService.getRecommendations(userId);
        log.info("response GET /users/{}/recommendations, response size = {}", userId, result.size());
        return result;
    }
}
