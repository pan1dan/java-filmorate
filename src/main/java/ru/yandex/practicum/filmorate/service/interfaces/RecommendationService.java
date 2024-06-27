package ru.yandex.practicum.filmorate.service.interfaces;

import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.List;

public interface RecommendationService {
    List<Film> getRecommendations(final long userId);
}
