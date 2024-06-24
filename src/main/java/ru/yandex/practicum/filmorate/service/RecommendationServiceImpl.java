package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.service.interfaces.RecommendationService;
import ru.yandex.practicum.filmorate.storage.model.RecommendationStorage;
import ru.yandex.practicum.filmorate.storage.model.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {
    private final UserStorage userDbStorage;
    private final RecommendationStorage recommendationDbStorage;

    @Override
    public List<Film> getRecommendations(final long userId) {
        log.info("Вызов метода recommendationServiceImpl.getRecommendations() c userId = {}", userId);
        checkUser(userId);
        return recommendationDbStorage.getRecommendations(userId);
    }

    private void checkUser(final long userId) {
        log.info("Вызов метода recommendationServiceImpl.checkUser() c userId = {}", userId);
        final User user = userDbStorage.getUserById(userId);
        Optional.ofNullable(user)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %s не найден ", userId)));
    }
}
