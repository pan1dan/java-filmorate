package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);

    public Set<Long> addUserIdInFilmLikesList(Long filmId, Long userId, Storage storage) {
        log.info("Начало работы метода по добавлению id = {} пользователя " +
                "в лист лайков фильма c id = {}", userId, filmId);
        log.debug("Проверка на наличие пользователя в хранилище");
        User user = storage.getInMemoryUserStorage().getUserByIdFromStorage(userId);
        log.debug("Получение фильма из хранилища");
        Film film = storage.getInMemoryFilmStorage().getFilmByIdFromStorage(filmId);
        log.debug("Получение листа с id лайкнувших пользователей");
        Set<Long> filmLikesList = film.getLikesFromUsersList();
        log.debug("Добавление id пользоваетеля в лист лайков фильма");
        filmLikesList.add(userId);
        log.debug("Сохранение обновленного листа");
        film.setLikesFromUsersList(filmLikesList);
        log.info("Возвращение обновленного листа с id лайкнувших пользователей: {}", film.getLikesFromUsersList());
        return film.getLikesFromUsersList();
    }

    public Set<Long> deleteUserIdFromFilmLikesList(Long filmId, Long userId, Storage storage) {
        log.info("Начало работы метода по удалению id = {} пользователя " +
                "из листа лайков фильма c id = {}", userId, filmId);
        log.debug("Проверка на наличие пользователя в хранилище");
        storage.getInMemoryUserStorage().getUserByIdFromStorage(userId);
        log.debug("Получение фильма из хранилища");
        Film film = storage.getInMemoryFilmStorage().getFilmByIdFromStorage(filmId);
        log.debug("Получение листа с id лайкнувших пользователей");
        Set<Long> filmLikesList = film.getLikesFromUsersList();
        log.debug("Удаление id пользоваетеля из листа лайков фильма");
        filmLikesList.remove(userId);
        log.debug("Сохранение обновленного листа");
        film.setLikesFromUsersList(filmLikesList);
        log.info("Возвращение обновленного листа с id лайкнувших пользователей: {}", film.getLikesFromUsersList());
        return film.getLikesFromUsersList();
    }

    public List<Film> getTopFilmsByLikes(Integer count, InMemoryFilmStorage inMemoryFilmStorage) {
        log.info("Возвращение топа фильмов");
        return inMemoryFilmStorage.getAllFilmsFromStorage()
                .stream()
                .sorted((film1, film2) -> Integer.compare(film2.getLikesFromUsersList().size(), film1.getLikesFromUsersList().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
