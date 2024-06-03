package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

@Component
public class Storage {
    private static InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
    private static InMemoryFilmStorage inMemoryFilmStorage = new InMemoryFilmStorage();

    public InMemoryUserStorage getInMemoryUserStorage() {
        return inMemoryUserStorage;
    }

    public InMemoryFilmStorage getInMemoryFilmStorage() {
        return inMemoryFilmStorage;
    }
}
