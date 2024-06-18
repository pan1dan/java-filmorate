package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getAllFilms() {
        log.info("GET /films");
        return filmService.getAllFilms();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film addNewFilm(@RequestBody Film film) {
        log.info("POST /films");
        return filmService.addNewFilm(film);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Film updateFilm(@RequestBody Film newFilm) {
        log.info("PUT /films");
        return filmService.updateFilm(newFilm);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Film getFilmById(@PathVariable(name = "id") Long filmId) {
        log.info("GET /films/{}", filmId);
        return filmService.getFilmById(filmId);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void userLikeFilm(@PathVariable(name = "id") Long filmId,
                                  @PathVariable(name = "userId") Long userId) {
        log.info("PUT /films/{}/like/{}", filmId, userId);
        filmService.addUserIdInFilmLikesList(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void userDeleteLikeOnFilm(@PathVariable(name = "id") Long filmId,
                                          @PathVariable(name = "userId") Long userId) {
        log.info("DELETE /films/{}/like/{}", filmId, userId);
        filmService.deleteUserIdFromFilmLikesList(filmId, userId);
    }

    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getTopFilms(@RequestParam(defaultValue = "10") Integer count) {
        log.info("GET /films/popular?count={}", count);
        return filmService.getTopFilmsByLikes(count);
    }

    //          "/director/{directorId}?sortBy=[year,likes]"
//    @GetMapping("/director/{directorId}?sortBy=[year,likes]")
//    @ResponseStatus(HttpStatus.OK)
//    public List<Film> getTopDirectorFilms(@PathVariable(name = "directorId") Long directorId,
//                                       @RequestParam(name = "sortBy") String sort) {
//        log.info("GET /director/{}?sortBy={}", directorId, sort);
//        return filmService
//
//    }

}
