package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.Storage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    Storage storage = new Storage(new InMemoryUserStorage(), new InMemoryFilmStorage());
    FilmService filmService;

    @Autowired
    public FilmController(InMemoryFilmStorage inMemoryFilmStorage, FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getAllFilms() {
        log.info("GET /films");
        return storage.getInMemoryFilmStorage().getAllFilmsFromStorage();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film addNewFilm(@RequestBody Film film) {
        log.info("POST /films");
        return storage.getInMemoryFilmStorage().addNewFilmToStorage(film);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Film updateFilm(@RequestBody Film newFilm) {
        log.info("PUT /films");
        return storage.getInMemoryFilmStorage().updateFilmInStorage(newFilm);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Film getFilmById(@PathVariable(name = "id") Long filmId) {
        log.info("GET /films/{}", filmId);
        return storage.getInMemoryFilmStorage().getFilmByIdFromStorage(filmId);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Set<Long> userLikeFilm(@PathVariable(name = "id") Long filmId,
                                  @PathVariable(name = "userId") Long userId) {
        log.info("PUT /films/{}/like/{}", filmId, userId);
        return filmService.addUserIdInFilmLikesList(filmId, userId, storage);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Set<Long> userDeleteLikeOnFilm(@PathVariable(name = "id") Long filmId,
                                          @PathVariable(name = "userId") Long userId) {
        log.info("DELETE /films/{}/like/{}", filmId, userId);
        return filmService.deleteUserIdFromFilmLikesList(filmId, userId, storage);
    }

    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getTopFilms(@RequestParam(defaultValue = "10") Integer count) {
        log.info("GET /films/popular?count={}", count);
        return filmService.getTopFilmsByLikes(count, storage.getInMemoryFilmStorage());
    }

}
