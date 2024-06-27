package ru.yandex.practicum.filmorate.storage.model;

import ru.yandex.practicum.filmorate.model.user.UserEvent;

import java.util.List;

public interface UserEventStorage {

    List<UserEvent> getUserEvents(long userId);

    void addUserEvent(long userId, String eventType, String operation, long entityId);

}
