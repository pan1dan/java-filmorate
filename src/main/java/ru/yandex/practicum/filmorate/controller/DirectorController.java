package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.film.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@RestController
@RequestMapping("/directors")
public class DirectorController {
    private static final Logger log = LoggerFactory.getLogger(DirectorController.class);
    DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Director> getAllDirectors() {
        log.info("GET /directors");
        return directorService.getAllDirectors();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Director getDirectorById(@PathVariable(name = "id") Long directorId) {
        log.info("GET /directors/{}", directorId);
        return directorService.getDirectorById(directorId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Director addNewDirector(@RequestBody Director director) {
        log.info("POST /directors");
        return directorService.addNewDirector(director);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Director updateDirector(@RequestBody Director newDirector) {
        log.info("PUT /directors");
        return directorService.updateDirector(newDirector);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteDirector(@PathVariable(name = "id") Long directorId) {
        log.info("DELETE /directors/{}", directorId);
        directorService.deleteDirector(directorId);
    }


}
