package ru.yandex.practicum.filmorate.model.user;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

/**
 * CREATED: SHCHETININAS 23.06.2024
 * Класс отвечающий за событие, которые выполнял пользователь.
 * На основе данного класса формируется лента событий по пользователю.
 * eventType check ('LIKE', 'REVIEW', 'FRIEND')
 * operation check ('REMOVE', 'ADD', 'UPDATE')
 * entityId зависит от eventType и может принадлежать к одной из 3ех сущностей:
 * filmId, reviewId или userId
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "eventId")
public class UserEvent {

    long timestamp;
    @NotNull
    long userId;

    EventType eventType;
    Operation operation;

    @NotNull
    long eventId;

    @NotNull
    long entityId;

}
