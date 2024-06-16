package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.storage.db.GenresDbStorage;
import ru.yandex.practicum.filmorate.storage.model.GenresStorage;

import java.util.List;

@Service
public class GenresService {
    private final GenresStorage genresStorage;
    private static final Logger log = LoggerFactory.getLogger(GenresService.class);

    @Autowired
    public GenresService(@Qualifier("genresDbStorage") GenresDbStorage genresStorage) {
        this.genresStorage = genresStorage;
    }

    public List<Genre> getAllGenres() {
        return genresStorage.getAllGenres();
    }

    public Genre getGenreNameById(int genreId) {
        return genresStorage.getGenreNameById(genreId);
    }
}
