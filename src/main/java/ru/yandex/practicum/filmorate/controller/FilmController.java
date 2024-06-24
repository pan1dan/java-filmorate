package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFilmById(@PathVariable(name = "id") long filmId) {
        log.info("DELETE /films/{}", filmId);
        filmService.deleteFilmById(filmId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getAllFilms() {
        log.info("GET /films");
        List<Film> allFilms = filmService.getAllFilms();
        log.info("GET /films возвращает значение: {}", allFilms);
        return allFilms;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film addNewFilm(@RequestBody Film film) {
        log.info("POST /films");
        Film addedFilm = filmService.addNewFilm(film);
        log.info("POST /films возвращает значение: {}", addedFilm);
        return addedFilm;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Film updateFilm(@RequestBody Film newFilm) {
        log.info("PUT /films");
        Film updateFilm = filmService.updateFilm(newFilm);
        log.info("PUT /films возвращает значение: {}", updateFilm);
        return updateFilm;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Film getFilmById(@PathVariable(name = "id") Long filmId) {
        log.info("GET /films/{}", filmId);
        Film film = filmService.getFilmById(filmId);
        log.info("GET /films/{} возвращает значение: {}", filmId, film);
        return film;
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
        List<Film> topFilms = filmService.getTopFilmsByLikes(count);
        log.info("GET /films/popular?count={} возвращает значение: {}", count, topFilms);
        return topFilms;
    }

    @GetMapping("/director/{directorId}")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getTopDirectorFilms(@PathVariable(name = "directorId") Long directorId,
                                          @RequestParam(name = "sortBy") String sortType) {
        log.info("GET /director/{}?sortBy={}", directorId, sortType);
        List<Film> topDirectorsFilms = filmService.getTopDirectorFilms(directorId, sortType);
        log.info("GET /director/{}?sortBy={} возвращает значение: {}", directorId, sortType, topDirectorsFilms);
        return topDirectorsFilms;

    }

    @GetMapping("/common")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getCommonFilms(@RequestParam(name = "userId") long userId,
                                     @RequestParam(name = "friendId") long friendId) {
        log.info("GET /films/common?userId={}&friendId={}", userId, friendId);
        List<Film> commonFilms = filmService.getCommonFilms(userId, friendId);
        log.info("GET /films/common?userId={}&friendId={} возвращает значение: {}", userId, friendId, commonFilms);
        return commonFilms;
    }

}
