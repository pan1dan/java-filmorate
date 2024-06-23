package ru.yandex.practicum.filmorate.storage.model;

import java.util.Optional;

public interface LikeStateStorage {
    Optional<Integer> getCurrentState(long reviewId, long userId);
}