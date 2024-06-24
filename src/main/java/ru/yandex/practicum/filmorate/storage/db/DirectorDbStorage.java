package ru.yandex.practicum.filmorate.storage.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.film.Director;
import ru.yandex.practicum.filmorate.storage.model.DirectorStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Qualifier("directorDbStorage")
@Primary
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final Logger log = LoggerFactory.getLogger(DirectorDbStorage.class);

    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Director> getAllDirectors() {
        try {
            return jdbcTemplate.query("SELECT * FROM directors", this::mapRow);
        } catch (Exception e) {
            log.warn("Ошибка при получении всех режиссеров из БД");
            throw new NotFoundException("Ошибка при получении всех режиссеров из БД");
        }
    }

    @Override
    public Director getDirectorById(Long id) {
        try {
            String sql1 = "SELECT COUNT(*) FROM directors WHERE director_id = ?";
            int count = jdbcTemplate.queryForObject(sql1, Integer.class, id);
            if (count == 0) {
                log.warn("Режиссёр с id = {} отсутствует в БД", id);
                throw new NotFoundException("Режиссёр с id = " + id + " отсутствует в БД");
            }

            String sql2 = "SELECT director_id, director_name FROM directors WHERE director_id = ?";
            return jdbcTemplate.queryForObject(sql2, this::mapRow, id);
        } catch (NotFoundException e) {
            log.warn("Режиссёр с id = {} отсутствует в БД", id);
            throw new NotFoundException("Режиссёр с id = " + id + " отсутствует в БД");
        } catch (Exception e) {
            log.warn("Ошибка при получении режиссера фильма по id из БД", e);
            throw new RuntimeException("Ошибка при получении режиссера фильма по id из БД", e);
        }
    }

    @Override
    public Director addNewDirector(Director director) {
        directorValidation(director);
        try {
            SimpleJdbcInsert insert1 = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("directors")
                    .usingGeneratedKeyColumns("director_id");
            Long directorId = (long) insert1.executeAndReturnKey(
                    new MapSqlParameterSource("director_name", director.getName()));
            director.setId(directorId);
        } catch (ValidationException e) {
            log.warn("Ошибка при добавлении фильма в БД", e);
            throw new ValidationException("Ошибка при добавлении фильма в БД");
        } catch (Exception e) {
            log.warn("Ошибка при добавлении режиссера в БД", e);
            throw new ValidationException("Ошибка при добавлении режиссера в БД");
        }

        return director;
    }

    @Override
    public Director updateDirector(Director newDirector) {
        try {
            String sql = "UPDATE directors SET director_name = ? WHERE director_id = ?";
            int rowsUpdated = jdbcTemplate.update(sql, newDirector.getName(), newDirector.getId());

            if (rowsUpdated == 0) {
                log.warn("Режиссер с id " + newDirector.getId() + " не найден");
                throw new NotFoundException("Режиссер с id " + newDirector.getId() + " не найден");
            }
        } catch (NotFoundException e) {
            log.warn("Режиссер с id " + newDirector.getId() + " не найден");
            throw new NotFoundException("Режиссер с id " + newDirector.getId() + " не найден");
        } catch (Exception e) {
            log.warn("Ошибка при обновлении режиссера в БД", e);
            throw new RuntimeException("Ошибка при обновлении режиссера в БД", e);
        }

        return newDirector;
    }

    @Override
    public void deleteDirector(Long directorId) {
        try {
            jdbcTemplate.update("DELETE FROM directors WHERE director_id = ?", directorId);
            jdbcTemplate.update("DELETE FROM film_director WHERE director_id = ?", directorId);
        } catch (Exception e) {
            log.warn("Ошибка при удалении режиссера из БД", e);
            throw new RuntimeException("Ошибка при удалении режиссера из БД", e);
        }
    }

    private Director mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Director.builder()
                .name(rs.getString("director_name"))
                .id(rs.getLong("director_id"))
                .build();
    }

    public void directorValidation(Director director) {
        if (director.getName() == null || director.getName().isBlank()) {
            throw new ValidationException("Неправильное имя режиссера");
        }
    }
}
