package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.storage.model.FilmRatingMpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Qualifier("filmDbRatingMpaStorage")
@Primary
@Slf4j
public class FilmDbRatingMpaStorage implements FilmRatingMpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbRatingMpaStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getAllMpa() {
        try {
            String sql = "SELECT mpa_id, mpa_name FROM film_rating_mpa";
            return jdbcTemplate.query(sql, this::mapRow);
        } catch (Exception e) {
            log.warn("Ошибка при получении всех видов рейтингов mpa из БД");
            throw new RuntimeException("Ошибка при получении списка все рейтингов mpa фильма из БД", e);
        }
    }

    @Override
    public Mpa getMpaById(int id) {
        try {
            String sql1 = "SELECT COUNT(*) FROM film_rating_mpa WHERE mpa_id = ?";
            int count = jdbcTemplate.queryForObject(sql1, Integer.class, id);
            if (count == 0) {
                log.warn("Рейтинг mpa с id = {} отсутствует в БД", id);
                throw new NotFoundException("Рейтинг mpa с id = " + id + " отсутствует в БД");
            }

            String sql2 = "SELECT mpa_id, mpa_name FROM film_rating_mpa WHERE mpa_id = " + id;
            return jdbcTemplate.queryForObject(sql2, this::mapRow);
        } catch (NotFoundException e) {
            log.warn("Рейтинг mpa с id = {} отсутствует в БД", id);
            throw new NotFoundException("Рейтинг mpa с id = " + id + " отсутствует в БД");
        } catch (Exception e) {
            log.warn("Ошибка при получении рейтинга mpa фильма из БД");
            throw new RuntimeException("Ошибка при получении рейтинга mpa фильма из БД", e);
        }
    }

    private Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(rs.getInt("mpa_id"))
                .name(rs.getString("mpa_name"))
                .build();
    }
}
