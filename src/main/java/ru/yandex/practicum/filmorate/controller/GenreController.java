package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.service.GenresService;

import java.util.List;

@RestController
@RequestMapping("/genres")
@Slf4j
public class GenreController {
    GenresService genresService;

    @Autowired
    public GenreController(GenresService genresService) {
        this.genresService = genresService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Genre> getAllFilmsGenres() {
        log.info("GET /genres");
        List<Genre> allFilmsGenres = genresService.getAllGenres();
        log.info("GET /genres возвращает значение: {}", allFilmsGenres);
        return allFilmsGenres;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Genre getGenreNameById(@PathVariable(name = "id") int genreId) {
        log.info("GET /genres/{}", genreId);
        Genre genre = genresService.getGenreNameById(genreId);
        log.info("GET /genres/{} возвращает значение: {}", genreId, genre);
        return genre;
    }


}
