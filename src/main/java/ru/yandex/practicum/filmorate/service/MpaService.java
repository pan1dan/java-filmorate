package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.storage.model.FilmRatingMpaStorage;

import java.util.List;

@Service
public class MpaService {
    private final FilmRatingMpaStorage filmRatingMpaStorage;
    private static final Logger log = LoggerFactory.getLogger(MpaService.class);

    @Autowired
    public MpaService(@Qualifier("filmDbRatingMpaStorage") FilmRatingMpaStorage filmRatingMpaStorage) {
        this.filmRatingMpaStorage = filmRatingMpaStorage;
    }

    public List<Mpa> getAllFilmsMpaRatings() {
        return filmRatingMpaStorage.getAllMpa();
    }

    public Mpa geRatingMpaById(int mpaId) {
        return filmRatingMpaStorage.getMpaById(mpaId);
    }
}
