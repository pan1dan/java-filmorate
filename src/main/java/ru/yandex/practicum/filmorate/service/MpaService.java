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
        log.info("Начало работы метода по получению всех рейтингов mpa");
        return filmRatingMpaStorage.getAllMpa();
    }

    public Mpa geRatingMpaById(int mpaId) {
        log.info("Начало работы метода по получению рейтинга mpa по его id = {}", mpaId);
        return filmRatingMpaStorage.getMpaById(mpaId);
    }
}
