package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.storage.model.FilmDirectorStorage;
import ru.yandex.practicum.filmorate.storage.model.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Qualifier("filmDirectorDbStorage")
@Primary
@Slf4j
public class FilmDirectorDbStorage implements FilmDirectorStorage {
    private final JdbcTemplate jdbcTemplate;
    FilmStorage filmStorage;

    public FilmDirectorDbStorage(JdbcTemplate jdbcTemplate,
                                 @Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmStorage = filmStorage;
    }

    public List<Film> getDirectorFilms(Long directorId) {
        try {
            String sql = "SELECT film_id FROM film_director WHERE director_id = " + directorId;
            return jdbcTemplate.query(sql, this::mapRow);
        } catch (Exception e) {
            log.warn("Ошибка при получении всех фильмов режиссера из БД");
            throw new RuntimeException("Ошибка при получении всех фильмов режиссера из БД", e);
        }
    }

    private Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        return filmStorage.getFilmById(rs.getLong("film_id"));
    }

}
