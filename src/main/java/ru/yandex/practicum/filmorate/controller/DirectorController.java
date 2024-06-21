package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.film.Director;

import ru.yandex.practicum.filmorate.service.serviceModel.DirectorService;
import ru.yandex.practicum.filmorate.storage.model.DirectorStorage;

import java.util.List;

@RestController
@RequestMapping("/directors")
@Slf4j
public class DirectorController {
    DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Director> getAllDirectors() {
        log.info("GET /directors");
        List<Director> allDirectors = directorService.getAllDirectors();
        log.info("GET /directors возвращает значение: {}", allDirectors);
        return allDirectors;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Director getDirectorById(@PathVariable("id") long directorId) {
        log.info("GET /directors/{}", directorId);
        Director director = directorService.getDirectorById(directorId);
        log.info("GET /directors/{} возвращает значение: {}", directorId, director);
        return director;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Director addNewDirector(@RequestBody Director director) {
        log.info("POST /directors");
        Director addedDirector = directorService.addNewDirector(director);
        log.info("POST /directors возвращает значение: {}", addedDirector);
        return addedDirector;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Director updateDirector(@RequestBody Director newDirector) {
        log.info("PUT /directors");
        Director updateDirector = directorService.updateDirector(newDirector);
        log.info("PUT /directors возвращает значение: {}", updateDirector);
        return updateDirector;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteDirector(@PathVariable("id") long directorId) {
        log.info("DELETE /directors/{}", directorId);
        directorService.deleteDirector(directorId);
    }

}
