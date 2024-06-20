package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.film.Director;
import ru.yandex.practicum.filmorate.storage.model.DirectorStorage;

import java.util.List;

@Service
public class DirectorService {
    private static final Logger log = LoggerFactory.getLogger(DirectorService.class);
    DirectorStorage directorStorage;
    public DirectorService(@Qualifier("directorDbStorage") DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public List<Director> getAllDirectors() {
        log.info("Начало работы метода по получению всех режиссеров");
        return directorStorage.getAllDirectors();
    }

    public Director getDirectorById(Long directorId) {
        log.info("Начало работы метода по получению режиссера по id = {}", directorId);
        return directorStorage.getDirectorById(directorId);
    }

    public Director addNewDirector(Director director) {
        log.info("Начало работы метода по добавлению нового режиссера: {}", director);
        return directorStorage.addNewDirector(director);
    }

    public Director updateDirector(Director newDirector) {
        log.info("Начало работы метода по изменению режиссера: {}", newDirector);
        return directorStorage.updateDirector(newDirector);
    }

    public void deleteDirector(Long directorId) {
        log.info("Начало работы метода по удалению режиссера с id = {}", directorId);
        directorStorage.deleteDirector(directorId);
    }

}
