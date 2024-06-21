package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.storage.model.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.model.GenresStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Qualifier("filmDbGenreStorage")
@Primary
@Slf4j
public class FilmDbGenreStorage implements FilmGenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenresStorage genresStorage;

    public FilmDbGenreStorage(JdbcTemplate jdbcTemplate,
                              @Qualifier("genresDbStorage") GenresStorage genresStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genresStorage = genresStorage;
    }

    @Override
    public List<Genre> getAllFilmsGenres(Long id) {
        try {
            String sql = "SELECT genre_id FROM film_genre WHERE film_id = " + id;
            return jdbcTemplate.query(sql, this::mapRow);
        } catch (Exception e) {
            log.warn("Ошибка при получении всех жанров фильма из БД");
            throw new RuntimeException("Ошибка при получении списка жанров фильма", e);
        }
    }

    private Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
        return genresStorage.getGenreNameById(rs.getInt("genre_id"));
    }
}
