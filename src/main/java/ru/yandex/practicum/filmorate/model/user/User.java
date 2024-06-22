package ru.yandex.practicum.filmorate.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.UserLikesFilms;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class User {
    Long id;
    String email;
    String login;
    String name;
    LocalDate birthday;
    UserFriends userFriends;
    Set<UserLikesFilms> likesFilms;

    @JsonIgnore
    Set<UserEvent> events;
}
