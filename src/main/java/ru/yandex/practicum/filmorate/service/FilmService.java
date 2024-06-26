package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.UserLikesFilms;
import ru.yandex.practicum.filmorate.model.enums.SearchType;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.SortType;
import ru.yandex.practicum.filmorate.storage.model.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UsersLikesFilmsStorage usersLikesFilmsStorage;
    private final FilmDirectorStorage filmDirectorStorage;
    private final DirectorStorage directorStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("usersLikesFilmsDbStorage") UsersLikesFilmsStorage usersLikesFilmsStorage,
                       @Qualifier("filmDirectorDbStorage") FilmDirectorStorage filmDirectorDbStorage,
                       @Qualifier("directorDbStorage") DirectorStorage directorStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.usersLikesFilmsStorage = usersLikesFilmsStorage;
        this.filmDirectorStorage = filmDirectorDbStorage;
        this.directorStorage = directorStorage;
        this.userStorage = userStorage;
    }

    public void deleteFilmById(long filmId) {
        log.info("Начало работы метода по удалению фильма с id = {}", filmId);
        filmStorage.deleteFilmById(filmId);
    }

    public List<Film> getAllFilms() {
        log.info("Начало работы метода по получению всех фильмов");
        return filmStorage.getAllFilm();
    }

    public Film addNewFilm(Film film) {
        log.info("Начало работы метода по добавлению нового фильма: {}", film);
        return filmStorage.create(film);
    }

    public Film updateFilm(Film newFilm) {
        log.info("Начало работы метода по обновлению фильма: {}", newFilm);
        return filmStorage.update(newFilm);
    }

    public Film getFilmById(Long filmId) {
        log.info("Начало работы метода по получению фильма по id = {}", filmId);
        return filmStorage.getFilmById(filmId);
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
        return filmStorage.getAllFilm()
                .stream()
                .sorted((film2, film1) -> Integer.compare(usersLikesFilmsStorage.getLikesCount(film1.getId()),
                        usersLikesFilmsStorage.getLikesCount(film2.getId())))
                .limit(count)
                .collect(Collectors.toList());
    }

    public List<Film> getTopDirectorFilms(Long directorId, String sortField) {
        log.info("Начало работы метода по возвращение топа фильмов режиссера");
        directorStorage.getDirectorById(directorId);
        List<Film> directorFilms = filmDirectorStorage.getDirectorFilms(directorId);
        SortType sortType = SortType.fromString(sortField);
        switch (sortType) {
            case YEAR:
                directorFilms = directorFilms.stream()
                        .sorted((film1, film2) -> film1.getReleaseDate().compareTo(film2.getReleaseDate()))
                        .toList();
                break;
            case LIKES:
                directorFilms = directorFilms.stream()
                        .sorted((film2, film1) -> Integer.compare(usersLikesFilmsStorage.getLikesCount(film1.getId()),
                                usersLikesFilmsStorage.getLikesCount(film2.getId())))
                        .toList();
                break;
        }

        return directorFilms;
    }

    public List<Film> getSearchFilms(String query, List<String> by) {
        SearchType searchType = getSearchType(by);
        return filmStorage.getSearchFilms(query, searchType);
    }

    private SearchType getSearchType(List<String> by) {
        if ((by.size() == 2)
                && ((by.get(0).equals("director") && (by.get(1).equals("title")))
                || (by.get(0).equals("title") && (by.get(1).equals("director"))))) {
            return SearchType.TITLE_AND_DIRECTOR;
        } else if (by.size() == 1 && by.get(0).equals("director")) {
            return SearchType.DIRECTOR;
        } else {
        return SearchType.TITLE;
        }
    }

    public List<Film> getCommonFilms(long userId, long friendId) {
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);
        return usersLikesFilmsStorage.getCommonFilms(userId, friendId);

    }
}
