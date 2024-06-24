package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.storage.model.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.model.FilmDirectorStorage;
import ru.yandex.practicum.filmorate.storage.model.FilmStorage;
import ru.yandex.practicum.filmorate.storage.model.UsersLikesFilmsStorage;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);
    private final FilmStorage filmStorage;
    private final UsersLikesFilmsStorage usersLikesFilmsStorage;
    private final FilmDirectorStorage filmDirectorStorage;
    private final DirectorStorage directorStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("usersLikesFilmsDbStorage") UsersLikesFilmsStorage usersLikesFilmsStorage,
                       @Qualifier("filmDirectorDbStorage")FilmDirectorStorage filmDirectorDbStorage,
                       @Qualifier("directorDbStorage")DirectorStorage directorStorage) {
        this.filmStorage = filmStorage;
        this.usersLikesFilmsStorage = usersLikesFilmsStorage;
        this.filmDirectorStorage = filmDirectorDbStorage;
        this.directorStorage = directorStorage;
    }

    public void deleteFilmById(Long filmId) {
        log.info("Начало работы метода по удалению фильма с id = {}", filmId);
        filmStorage.deleteFilmByIdFromStorage(filmId);
    }

    public List<Film> getAllFilms() {
        log.info("Начало работы метода по получению всех фильмов");
        return filmStorage.getAllFilmsFromStorage();
    }

    public Film addNewFilm(Film film) {
        log.info("Начало работы метода по добавлению нового фильма: {}", film);
        return filmStorage.addNewFilmToStorage(film);
    }

    public Film updateFilm(Film newFilm) {
        log.info("Начало работы метода по обновлению фильма: {}", newFilm);
        return filmStorage.updateFilmInStorage(newFilm);
    }

    public Film getFilmById(Long filmId) {
        log.info("Начало работы метода по получению фильма по id = {}", filmId);
        return filmStorage.getFilmByIdFromStorage(filmId);
    }

    public void addUserIdInFilmLikesList(Long filmId, Long userId) {
        log.info("Начало работы метода по добавлению id = {} пользователя " +
                "в лист лайков фильма c id = {}", userId, filmId);
        usersLikesFilmsStorage.addLikeFilm(filmId, userId);
    }

    public void deleteUserIdFromFilmLikesList(Long filmId, Long userId) {
        log.info("Начало работы метода по удалению id = {} пользователя " +
                "из листа лайков фильма c id = {}", userId, filmId);
        usersLikesFilmsStorage.deleteLikeFilm(filmId, userId);
    }

    public List<Film> getTopFilmsByLikes(Integer count) {
        log.info("Начало работы метода по возвращение топа фильмов");
        return filmStorage.getAllFilmsFromStorage()
                .stream()
                .sorted((film2, film1) -> Integer.compare(usersLikesFilmsStorage.getLikesCount(film1.getId()),
                        usersLikesFilmsStorage.getLikesCount(film2.getId())))
                .limit(count)
                .collect(Collectors.toList());
    }

    public List<Film> getTopDirectorFilmsByLikesOrYear(Long directorId, String sortField) {
        log.info("Начало работы метода по возвращение топа фильмов режиссера");
        directorStorage.getDirectorById(directorId);
        List<Film> directorFilms = filmDirectorStorage.getDirectorFilms(directorId);
        if (sortField.equals("year")) {
            directorFilms = directorFilms
                    .stream()
                    .sorted((film1, film2) -> film1.getReleaseDate().compareTo(film2.getReleaseDate()))
                    .toList();
        }
        if (sortField.equals("likes")) {
            directorFilms = directorFilms.stream()
                    .sorted((film2, film1) -> Integer.compare(usersLikesFilmsStorage.getLikesCount(film1.getId()),
                            usersLikesFilmsStorage.getLikesCount(film2.getId())))
                    .toList();
        }
        return directorFilms;
    }
}
