package ru.yandex.practicum.filmorate.storage.InMemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.storage.model.FilmStorage;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Component
@Qualifier("inMemoryFilmStorage")
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private static final LocalDate BIRTHDAY_OF_THE_MOVIE = LocalDate.of(1895, 12, 28);
    Map<Long, Film> films = new HashMap<>();

    @Override
    public List<Film> getAllFilmsFromStorage() {
        log.debug("Возврат списка всех фильмов: {}", films.values());
        return films.values().stream().toList();
    }

    @Override
    public Film addNewFilmToStorage(Film film) {
        if (film == null) {
            log.warn("Получено пустое тело запроса");
            throw new ValidationException("Получено пустое тело запроса");
        }
        log.debug("Получен объект: {}", film);
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("В запросе отсутствует название фильма");
            throw new ValidationException("Название фильма должно быть обязательно");
        }
        if (film.getDescription() == null) {
            film.setDescription("");
        }
        if (film.getDescription().length() > 200) {
            log.warn("В запросе передана слишком большая строка");
            throw new ValidationException("Длина описания должна быть меньше 200 символов");
        }
        if (film.getReleaseDate() == null) {
            log.warn("В запросе нет даты создания фильма");
            throw new ValidationException("В запросе нет даты создания фильма");
        }
        if (film.getReleaseDate().isBefore(BIRTHDAY_OF_THE_MOVIE)) {
            log.warn("В запросе передана невозможная дата");
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895");
        }
        if (film.getDuration() == null || film.getDuration() <= 0) {
            log.warn("В запросе передана невозможная длительность фильма");
            throw new ValidationException("Длительность фильма должна быть положительной");
        }
        log.debug("Присвоение id новому фильму");
        film.setId(getNextId());
        film.setUserLikesFilms(new HashSet<>());
        log.info("Добавлен новый фильм");
        films.put(film.getId(), film);
        log.debug("Возврат фильма: {}", films.get(film.getId()));
        return films.get(film.getId());
    }

    @Override
    public Film updateFilmInStorage(@RequestBody Film newFilm) {
        if (newFilm == null) {
            log.warn("Получено пустое запроса");
            throw new ValidationException("Получено пустое запроса");
        }
        log.debug("Получен объект: {}", newFilm);
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
            if (newFilm.getDuration() != null && newFilm.getDuration() > 0) {
                log.debug("Обновление длительности фильма с id = {}", newFilm.getId());
                films.get(newFilm.getId()).setDuration(newFilm.getDuration());
            }
            log.info("Данные фильма обновлены. Возвращён объект: {}", films.get(newFilm.getId()));
            return films.get(newFilm.getId());
        }
        log.warn("Фильм с переданным id = {} не найден", newFilm.getId());
        throw new NotFoundException("Фильма с id " + newFilm.getId() + " не найден");
    }

    public Film getFilmByIdFromStorage(Long filmId) {
        if (!films.containsKey(filmId)) {
            log.warn("Фильм с переданным id = {} не найден", filmId);
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
        return films.get(filmId);
    }

    private Long getNextId() {
        log.info("Запуск метода определения нового id");
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        log.info("Возвращение нового уникального id = {}", currentMaxId + 1);
        return ++currentMaxId;
    }
}
