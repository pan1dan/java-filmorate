package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.storage.model.GenresStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Qualifier("genresDbStorage")
@Primary
@Slf4j
public class GenresDbStorage implements GenresStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenresDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getAllGenres() {
        try {
            String sql = "SELECT genre_id, genre_name FROM genres";
            return jdbcTemplate.query(sql, this::mapRow);
        } catch (Exception e) {
            log.warn("Ошибка при получении списка все жанров фильмов из БД", e);
            throw new RuntimeException("Ошибка при получении списка все жанров фильмов из БД", e);
        }
    }

    @Override
    public Genre getGenreNameById(int id) {
        try {
            String sql1 = "SELECT COUNT(*) FROM genres WHERE genre_id = ?";
            int count = jdbcTemplate.queryForObject(sql1, Integer.class, id);
            if (count == 0) {
                log.warn("Жанр с id = {} отсутствует в БД", id);
                throw new NotFoundException("Жанр с id = " + id + " отсутствует в БД");
            }

            String sql = "SELECT genre_id, genre_name FROM genres WHERE genre_id = " + id;
            return jdbcTemplate.queryForObject(sql, this::mapRow);
        } catch (NotFoundException e) {
            log.warn("Жанр с id = {} отсутствует в БД", id);
            throw new NotFoundException("Жанр с id = " + id + " отсутствует в БД");
        } catch (Exception e) {
            log.warn("Ошибка при получении жанра фильма по id из БД", e);
            throw new RuntimeException("Ошибка при получении жанра фильма по id из БД", e);
        }
    }

    private Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("genre_name"))
                .build();
    }
}
