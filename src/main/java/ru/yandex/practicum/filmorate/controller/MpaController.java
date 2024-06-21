package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@Slf4j
public class MpaController {
    MpaService mpaService;

    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping
    public List<Mpa> getAllFilmsMpaRatings() {
        log.info("GET /mpa");
        List<Mpa> allFilmsMpaRatings = mpaService.getAllFilmsMpaRatings();
        log.info("GET /mpa возвращает значение: {}", allFilmsMpaRatings);
        return allFilmsMpaRatings;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mpa getMpaById(@PathVariable(name = "id") int mpaId) {
        log.info("GET /mpa/{}", mpaId);
        Mpa mpa = mpaService.geRatingMpaById(mpaId);
        log.info("GET /mpa/{} возвращает значение: {}", mpaId, mpa);
        return mpa;
    }
}
