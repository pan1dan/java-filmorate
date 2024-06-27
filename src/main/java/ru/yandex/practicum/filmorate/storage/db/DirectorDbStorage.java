package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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
@Slf4j
public class DirectorDbStorage implements DirectorStorage {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public DirectorDbStorage(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public List<Director> getAllDirectors() {
        try {
            return namedParameterJdbcTemplate.query("SELECT * FROM directors", this::mapRow);
        } catch (Exception e) {
            log.warn("Ошибка при получении всех режиссеров из БД");
            throw new NotFoundException("Ошибка при получении всех режиссеров из БД");
        }
    }

    @Override
    public Director getDirectorById(Long id) {
        try {
            String checkDirectorByIdSqlQuery = "SELECT COUNT(*) FROM directors WHERE director_id = :id";
            MapSqlParameterSource params1 = new MapSqlParameterSource().addValue("id", id);
            int count = namedParameterJdbcTemplate.queryForObject(checkDirectorByIdSqlQuery, params1, Integer.class);
            if (count == 0) {
                log.warn("Режиссёр с id = {} отсутствует в БД", id);
                throw new NotFoundException("Режиссёр с id = " + id + " отсутствует в БД");
            }

            String getDirectorByIdSqlQuery = "SELECT director_id, director_name FROM directors WHERE director_id = :id";
            return namedParameterJdbcTemplate.queryForObject(getDirectorByIdSqlQuery, params1, this::mapRow);
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
        try {
            SimpleJdbcInsert insertNewDirectorSql = new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate())
                    .withTableName("directors")
                    .usingGeneratedKeyColumns("director_id");
            Long directorId = (long) insertNewDirectorSql.executeAndReturnKey(
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
            String updateDirectorSql = "UPDATE directors SET director_name = :name WHERE director_id = :id";
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("name", newDirector.getName())
                    .addValue("id", newDirector.getId());
            int rowsUpdated = namedParameterJdbcTemplate.update(updateDirectorSql, params);

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
            MapSqlParameterSource directorParameters = new MapSqlParameterSource().addValue("id", directorId);
            namedParameterJdbcTemplate.update("DELETE FROM directors WHERE director_id = :id", directorParameters);
            namedParameterJdbcTemplate.update("DELETE FROM film_director WHERE director_id = :id", directorParameters);
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

}
