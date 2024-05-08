package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    Map<Long, Film> films = new HashMap<>();
    private static final LocalDate BIRTHDAY_OF_THE_MOVIE = LocalDate.of(1895, 12, 28);
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("GET /films");
        log.debug("Возврат списка всех фильмов");
        return films.values();
    }

    @PostMapping
    public Film addNewFilm(@RequestBody Film film) {
        log.info("POST /films");
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("В запросе отсутствует название фильма");
            throw new ValidationException("Название фильма должно быть обязательно");
        }
        if (film.getDescription().length() > 200) {
            log.warn("В запросе передана слишком большая строка");
            throw new ValidationException("Длина описания должна быть меньше 200 символов");
        }
        if (film.getReleaseDate().isBefore(BIRTHDAY_OF_THE_MOVIE)) {
            log.warn("В запросе передана невозможная дата");
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895");
        }
        if (film.getDuration() < 0) {
            log.warn("В запросе передана невозможная длительность фильма");
            throw new ValidationException("Длительность фильма не может быть отрицательной");
        }
        log.trace("Присвоение id новому фильму");
        film.setId(getNextId());
        log.info("Добавлен новый фильм");
        films.put(film.getId(), film);
        log.debug("Возврат фильма в теле ответа");
        return films.get(film.getId());
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film newFilm) {
        log.info("PUT /films");
        if (newFilm.getId() == null) {
            log.warn("В запросе на изменение не был передан id");
            throw new ValidationException("Поле id не может быть пустым");
        }
        log.trace("Начата проверка на наличие фильма в списке фильмов по id = {}", newFilm.getId());
        if (films.containsKey(newFilm.getId())) {
            if (newFilm.getName() != null && !newFilm.getName().isBlank()) {
                log.debug("Обновление имени фильма с id = {}", newFilm.getId());
                films.get(newFilm.getId()).setName(newFilm.getName());
            }
            if (newFilm.getDescription() != null && newFilm.getDescription().length() <= 200) {
                log.debug("Обновление описания фильма с id = {}", newFilm.getId());
                films.get(newFilm.getId()).setDescription(newFilm.getDescription());
            }
            if (newFilm.getReleaseDate() != null && newFilm.getReleaseDate().isAfter(BIRTHDAY_OF_THE_MOVIE)) {
                log.debug("Обновление даты релиза фильма с id = {}", newFilm.getId());
                films.get(newFilm.getId()).setReleaseDate(newFilm.getReleaseDate());
            }
            if (newFilm.getDuration() != null && newFilm.getDuration() >= 0) {
                log.debug("Обновление длительности фильма с id = {}", newFilm.getId());
                films.get(newFilm.getId()).setDuration(newFilm.getDuration());
            }
            log.info("Данные фильма обновлены");
            return films.get(newFilm.getId());
        }
        log.warn("Фильм с переданным id = {} не найден", newFilm.getId());
        throw new ValidationException("Фильма с id " + newFilm.getId() + " не найден");
    }

    private Long getNextId() {
        log.info("Запуск метода определения нового id");
        log.trace("Определение текущего максимульного id");
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        log.info("Возвращение нового уникального id = {}", currentMaxId + 1);
        return ++currentMaxId;
    }
}
