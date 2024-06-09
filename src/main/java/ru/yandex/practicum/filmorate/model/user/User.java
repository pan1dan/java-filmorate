package ru.yandex.practicum.filmorate.model.user;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Set;

@Data
@Builder
public class User {
    Long id;
    String email;
    String login;
    String name;
    LocalDate birthday;
    Set<Long> friendsList;
    HashMap<Long, friendshipStatus> friendsStatusList;
}
