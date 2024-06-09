package ru.yandex.practicum.filmorate.model.film;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class Film {
    Long id;
    String name;
    String description;
    LocalDate releaseDate;
    Integer duration;
    Set<Long> likesFromUsersList;
    List<Genres> genre;
    MPA ratingMPA;
}
