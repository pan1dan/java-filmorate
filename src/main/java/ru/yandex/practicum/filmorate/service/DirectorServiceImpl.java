package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.film.Director;
import ru.yandex.practicum.filmorate.service.interfaces.DirectorService;
import ru.yandex.practicum.filmorate.storage.model.DirectorStorage;

import java.util.List;

@Service
@Slf4j
public class DirectorServiceImpl implements DirectorService {
    private final DirectorStorage directorStorage;

    public DirectorServiceImpl(@Qualifier("directorDbStorage") DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    @Override
    public List<Director> getAllDirectors() {
        log.info("Начало работы метода по получению всех режиссеров");
        return directorStorage.getAllDirectors();
    }

    @Override
    public Director getDirectorById(Long directorId) {
        log.info("Начало работы метода по получению режиссера по id = {}", directorId);
        return directorStorage.getDirectorById(directorId);
    }

    @Override
    public Director addNewDirector(Director director) {
        directorValidation(director);
        log.info("Начало работы метода по добавлению нового режиссера: {}", director);
        return directorStorage.addNewDirector(director);
    }

    @Override
    public Director updateDirector(Director newDirector) {
        log.info("Начало работы метода по изменению режиссера: {}", newDirector);
        return directorStorage.updateDirector(newDirector);
    }

    @Override
    public void deleteDirector(Long directorId) {
        log.info("Начало работы метода по удалению режиссера с id = {}", directorId);
        directorStorage.deleteDirector(directorId);
    }

    private void directorValidation(Director director) {
        if (director.getName() == null || director.getName().isBlank()) {
            throw new ValidationException("Неправильное имя режиссера");
        }
    }

}
